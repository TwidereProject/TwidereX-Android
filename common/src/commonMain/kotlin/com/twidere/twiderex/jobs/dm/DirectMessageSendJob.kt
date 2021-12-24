/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
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

import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.kmp.FileResolver
import com.twidere.twiderex.kmp.MediaSize
import com.twidere.twiderex.kmp.ResLoader
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.job.DirectMessageSendData
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.navigation.RootDeepLinks
import com.twidere.twiderex.notification.AppNotification
import com.twidere.twiderex.notification.AppNotificationManager
import com.twidere.twiderex.notification.NotificationChannelSpec
import com.twidere.twiderex.notification.notificationChannelId
import com.twidere.twiderex.repository.AccountRepository

abstract class DirectMessageSendJob<T : MicroBlogService>(
    protected val cacheDatabase: CacheDatabase,
    private val accountRepository: AccountRepository,
    private val notificationManager: AppNotificationManager,
    protected val fileResolver: FileResolver,
    private val resLoader: ResLoader,
) {
    suspend fun execute(sendData: DirectMessageSendData, accountKey: MicroBlogKey) {
        val accountDetails = accountKey.let {
            accountRepository.findByAccountKey(accountKey = it)
        } ?: throw Error("can't find any account matches:$accountKey")
        val notificationId = sendData.draftMessageKey.hashCode()
        @Suppress("UNCHECKED_CAST")
        val service = accountDetails.service as T

        var draftEvent: UiDMEvent? = null
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
                    .insertAll(listOf(draftEvent.copy(sendStatus = UiDMEvent.SendStatus.FAILED)))
            }
            val builder = AppNotification
                .Builder(
                    accountDetails.accountKey.notificationChannelId(
                        NotificationChannelSpec.ContentMessages.id
                    )
                )
                .setContentTitle(resLoader.getString(com.twidere.twiderex.MR.strings.common_alerts_failed_to_send_message_message))
                .setContentText(sendData.text)
                .setDeepLink(RootDeepLinks.Conversation(sendData.conversationKey))
            notificationManager.notify(notificationId, builder.build())
            throw e
        }
    }

    private suspend fun updateDb(draftEvent: UiDMEvent?, dbEvent: UiDMEvent) {
        cacheDatabase.withTransaction {
            draftEvent?.let {
                cacheDatabase.directMessageDao().delete(
                    it
                )
            }
            cacheDatabase.directMessageDao().insertAll(listOf(dbEvent))
        }
    }

    private suspend fun getDraft(sendData: DirectMessageSendData, images: List<String>, account: AccountDetails): UiDMEvent? {
        return cacheDatabase.withTransaction {
            cacheDatabase.directMessageDao().findWithMessageKey(
                account.accountKey,
                sendData.conversationKey,
                sendData.draftMessageKey
            )?.also {
                cacheDatabase.directMessageDao().insertAll(
                    listOf(it.copy(sendStatus = UiDMEvent.SendStatus.PENDING))
                )
            }
        } ?: saveDraft(sendData, images, account)
    }

    private suspend fun saveDraft(sendData: DirectMessageSendData, images: List<String>, account: AccountDetails): UiDMEvent? {
        return cacheDatabase.withTransaction {
            val createTimeStamp = System.currentTimeMillis()
            listOf(
                UiDMEvent(
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
                    sendStatus = UiDMEvent.SendStatus.PENDING,
                    media = images.mapIndexed { index, uri ->
                        val imageSize = getImageSize(uri)
                        UiMedia(
                            belongToKey = sendData.draftMessageKey,
                            url = uri,
                            mediaUrl = uri,
                            previewUrl = uri,
                            type = getMediaType(uri),
                            width = imageSize.width,
                            height = imageSize.height,
                            altText = "",
                            order = index,
                            pageUrl = null,
                        )
                    },
                    urlEntity = emptyList(),
                    sender = account.toUi()
                )
            ).let {
                cacheDatabase.directMessageDao().insertAll(it)
            }
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

    private fun getImageSize(path: String): MediaSize {
        return fileResolver.getMediaSize(path)
    }

    protected abstract suspend fun sendMessage(
        service: T,
        sendData: DirectMessageSendData,
        mediaIds: ArrayList<String>
    ): UiDMEvent

    protected abstract suspend fun uploadImage(
        originUri: String,
        scramblerUri: String,
        service: T
    ): String?

    protected abstract suspend fun autoLink(text: String): String
}
