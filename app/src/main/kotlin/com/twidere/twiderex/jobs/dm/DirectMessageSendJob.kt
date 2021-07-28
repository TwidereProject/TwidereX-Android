/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.jobs.dm

import android.content.Context
import android.graphics.BitmapFactory
import androidx.room.withTransaction
import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.R
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.model.DbDMEvent
import com.twidere.twiderex.db.model.DbDMEvent.Companion.saveToDb
import com.twidere.twiderex.db.model.DbDMEventWithAttachments
import com.twidere.twiderex.db.model.DbDMEventWithAttachments.Companion.saveToDb
import com.twidere.twiderex.db.model.DbMedia
import com.twidere.twiderex.kmp.FileResolver
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.DirectMessageSendData
import com.twidere.twiderex.model.MediaType
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.navigation.RootDeepLinksRoute
import com.twidere.twiderex.notification.AppNotification
import com.twidere.twiderex.notification.AppNotificationManager
import com.twidere.twiderex.notification.NotificationChannelSpec
import com.twidere.twiderex.notification.notificationChannelId
import com.twidere.twiderex.repository.AccountRepository
import java.net.URI
import java.util.UUID

abstract class DirectMessageSendJob<T : MicroBlogService>(
    private val applicationContext: Context,
    protected val cacheDatabase: CacheDatabase,
    private val accountRepository: AccountRepository,
    private val notificationManager: AppNotificationManager,
    protected val fileResolver: FileResolver,
) {
    suspend fun execute(sendData: DirectMessageSendData, accountKey: MicroBlogKey) {
        val accountDetails = accountKey.let {
            accountRepository.findByAccountKey(accountKey = it)
        }?.let {
            accountRepository.getAccountDetails(it)
        } ?: throw Error("can't find any account matches:$accountKey")
        val notificationId = sendData.draftMessageKey.hashCode()
        @Suppress("UNCHECKED_CAST")
        val service = accountDetails.service as T

        var draftEvent: DbDMEventWithAttachments? = null
        try {
            val images = sendData.images
            draftEvent = getDraft(sendData, images, accountDetails) ?: throw IllegalArgumentException()
            // val exifScrambler = ExifScrambler(context)
            val mediaIds = arrayListOf<String>()

            images.forEach { uri ->
                // val scramblerUri = exifScrambler.removeExifData(uri)
                // TODO FIXME 2020/6/30 Twitter DM throws bad media error after remove exif data from images
                //
                val id = uploadImage(uri, uri, service)
                id?.let { mediaIds.add(it) }
                // exifScrambler.deleteCacheFile(scramblerUri)
            }
            val dbEvent = sendMessage(service, sendData, mediaIds)
            updateDb(draftEvent, dbEvent)
        } catch (e: Throwable) {
            e.printStackTrace()
            draftEvent?.let {
                cacheDatabase.directMessageDao()
                    .insertAll(listOf(draftEvent.message.copy(sendStatus = DbDMEvent.SendStatus.FAILED)))
            }
            val builder = AppNotification
                .Builder(
                    accountDetails.accountKey.notificationChannelId(
                        NotificationChannelSpec.ContentMessages.id
                    )
                )
                .setContentTitle(applicationContext.getString(R.string.common_alerts_failed_to_send_message_message))
                .setContentText(sendData.text)
                .setDeepLink(RootDeepLinksRoute.Conversation(sendData.conversationKey))
            notificationManager.notify(notificationId, builder.build())
            throw e
        }
    }

    private suspend fun updateDb(draftEvent: DbDMEventWithAttachments?, dbEvent: DbDMEventWithAttachments) {
        cacheDatabase.withTransaction {
            draftEvent?.let {
                cacheDatabase.directMessageDao().delete(
                    it.message
                )
            }
            listOf(dbEvent).saveToDb(cacheDatabase)
        }
    }

    private suspend fun getDraft(sendData: DirectMessageSendData, images: List<String>, account: AccountDetails): DbDMEventWithAttachments? {
        return cacheDatabase.withTransaction {
            cacheDatabase.directMessageDao().findWithMessageKey(
                account.accountKey,
                sendData.conversationKey,
                sendData.draftMessageKey
            )?.also {
                cacheDatabase.directMessageDao().insertAll(
                    listOf(it.message.copy(sendStatus = DbDMEvent.SendStatus.PENDING))
                )
            }
        } ?: saveDraft(sendData, images, account)
    }

    private suspend fun saveDraft(sendData: DirectMessageSendData, images: List<String>, account: AccountDetails): DbDMEventWithAttachments? {
        return cacheDatabase.withTransaction {
            val createTimeStamp = System.currentTimeMillis()
            listOf(
                DbDMEvent(
                    _id = UUID.randomUUID().toString(),
                    accountKey = account.accountKey,
                    sortId = createTimeStamp,
                    conversationKey = sendData.conversationKey,
                    messageId = sendData.draftMessageKey.id,
                    messageKey = sendData.draftMessageKey,
                    htmlText = autoLink(sendData.text ?: ""),
                    originText = sendData.text ?: "",
                    createdTimestamp = createTimeStamp,
                    messageType = "message_create",
                    senderAccountKey = account.accountKey,
                    recipientAccountKey = sendData.recipientUserKey,
                    sendStatus = DbDMEvent.SendStatus.PENDING
                )
            ).saveToDb(cacheDatabase)
            cacheDatabase.mediaDao().insertAll(
                images.mapIndexed { index, uri ->
                    val imageSize = getImageSize(URI.create(uri).path)
                    DbMedia(
                        _id = UUID.randomUUID().toString(),
                        belongToKey = sendData.draftMessageKey,
                        url = uri.toString(),
                        mediaUrl = uri.toString(),
                        previewUrl = uri.toString(),
                        type = getMediaType(uri),
                        width = imageSize[0],
                        height = imageSize[1],
                        altText = "",
                        order = index,
                        pageUrl = null,
                    )
                }
            )
            cacheDatabase.directMessageDao().findWithMessageKey(
                account.accountKey,
                sendData.conversationKey,
                sendData.draftMessageKey
            )
        }
    }

    private fun getMediaType(uri: String): MediaType {
        val type = fileResolver.getMimeType(uri) ?: ""
        return when {
            type.startsWith("image") -> MediaType.photo
            type.startsWith("video") -> MediaType.video
            else -> MediaType.other
        }
    }

    private fun getImageSize(path: String?): Array<Long> {
        return path?.let {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(it, options)
            arrayOf(
                options.outWidth.toLong(),
                options.outHeight.toLong()
            )
        } ?: arrayOf(0, 0)
    }

    protected abstract suspend fun sendMessage(
        service: T,
        sendData: DirectMessageSendData,
        mediaIds: ArrayList<String>
    ): DbDMEventWithAttachments

    protected abstract suspend fun uploadImage(
        originUri: String,
        scramblerUri: String,
        service: T
    ): String?

    protected abstract suspend fun autoLink(text: String): String
}
