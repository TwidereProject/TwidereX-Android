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
package com.twidere.twiderex.worker.dm

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.R
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.model.DbDMEvent
import com.twidere.twiderex.db.model.DbDMEvent.Companion.saveToDb
import com.twidere.twiderex.db.model.DbDMEventWithAttachments
import com.twidere.twiderex.db.model.DbDMEventWithAttachments.Companion.saveToDb
import com.twidere.twiderex.db.model.DbMedia
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.DirectMessageSendData
import com.twidere.twiderex.model.MediaType
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.toDirectMessageSendData
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.notification.NotificationChannelSpec
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.utils.ExifScrambler
import kotlinx.coroutines.delay
import java.util.UUID

abstract class DirectMessageSendWorker<T : MicroBlogService>(
    protected val context: Context,
    workerParams: WorkerParameters,
    protected val cacheDatabase: CacheDatabase,
    protected val contentResolver: ContentResolver,
    private val accountRepository: AccountRepository,
    private val notificationManagerCompat: NotificationManagerCompat,
) : CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {
        val sendData = inputData.toDirectMessageSendData()
        val accountDetails = inputData.getString("accountKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let {
            accountRepository.findByAccountKey(accountKey = it)
        }?.let {
            accountRepository.getAccountDetails(it)
        } ?: return Result.failure()
        val notificationId = sendData.dratMessageKey.hashCode()
        @Suppress("UNCHECKED_CAST")
        val service = accountDetails.service as T
        var draftEvent: DbDMEventWithAttachments? = null
        return try {
            val images = sendData.images.map {
                Uri.parse(it)
            }
            draftEvent = getDraft(sendData, images, accountDetails) ?: throw IllegalArgumentException()
            val exifScrambler = ExifScrambler(context)
            val mediaIds = arrayListOf<String>()

            images.forEach { uri ->
                val scramblerUri = exifScrambler.removeExifData(uri)
                val id = uploadImage(uri, scramblerUri, service)
                id?.let { mediaIds.add(it) }
                exifScrambler.deleteCacheFile(scramblerUri)
            }
            delay(20000)
            throw Error()
            // val dbEvent = sendMessage(service, sendData, mediaIds)
            // updateDb(draftEvent, dbEvent)
            // Result.success()
        } catch (e: Throwable) {
            e.printStackTrace()
            draftEvent?.let {
                cacheDatabase.directMessageDao()
                    .insertAll(listOf(draftEvent.message.copy(sendStatus = DbDMEvent.SendStatus.FAILED)))
            }
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(Route.DeepLink.Conversation(sendData.conversationKey)))
            val pendingIntent =
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_MUTABLE
                )
            // TODO DM localize
            val builder = NotificationCompat
                .Builder(applicationContext, NotificationChannelSpec.BackgroundProgresses.id)
                .setContentTitle(applicationContext.getString(R.string.common_alerts_tweet_sending_title))
                .setSmallIcon(R.drawable.ic_notification)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setProgress(0, 0, false)
                .setSilent(false)
                .setAutoCancel(true)
                .setContentTitle("Failed to send message")
                .setContentText(sendData.text)
                .setContentIntent(pendingIntent)
            notificationManagerCompat.notify(notificationId, builder.build())
            Result.failure()
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

    private suspend fun getDraft(sendData: DirectMessageSendData, images: List<Uri>, account: AccountDetails): DbDMEventWithAttachments? {
        return cacheDatabase.withTransaction {
            cacheDatabase.directMessageDao().findWithMessageKey(
                account.accountKey,
                sendData.conversationKey,
                sendData.dratMessageKey
            )?.also {
                cacheDatabase.directMessageDao().insertAll(
                    listOf(it.message.copy(sendStatus = DbDMEvent.SendStatus.PENDING))
                )
            }
        } ?: saveDraft(sendData, images, account)
    }

    private suspend fun saveDraft(sendData: DirectMessageSendData, images: List<Uri>, account: AccountDetails): DbDMEventWithAttachments? {
        return cacheDatabase.withTransaction {
            val createTimeStamp = System.currentTimeMillis()
            listOf(
                DbDMEvent(
                    _id = UUID.randomUUID().toString(),
                    accountKey = account.accountKey,
                    sortId = createTimeStamp,
                    conversationKey = sendData.conversationKey,
                    messageId = sendData.dratMessageKey.id,
                    messageKey = sendData.dratMessageKey,
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
                    val imageSize = getImageSize(uri.path)
                    DbMedia(
                        _id = UUID.randomUUID().toString(),
                        belongToKey = sendData.dratMessageKey,
                        url = uri.toString(),
                        mediaUrl = uri.toString(),
                        previewUrl = uri.toString(),
                        type = MediaType.photo,
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
                sendData.dratMessageKey
            )
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
        originUri: Uri,
        scramblerUri: Uri,
        service: T
    ): String?

    protected abstract suspend fun autoLink(text: String): String
}
