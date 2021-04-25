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
package com.twidere.twiderex.worker.compose

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.R
import com.twidere.twiderex.model.ComposeData
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.toComposeData
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.notification.NotificationChannelSpec
import com.twidere.twiderex.repository.AccountRepository
import kotlin.math.roundToInt

abstract class ComposeWorker<T : MicroBlogService>(
    context: Context,
    workerParams: WorkerParameters,
    private val accountRepository: AccountRepository,
    private val notificationManagerCompat: NotificationManagerCompat,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val builder = NotificationCompat
            .Builder(applicationContext, NotificationChannelSpec.BackgroundProgresses.id)
            .setContentTitle(applicationContext.getString(R.string.common_alerts_tweet_sending_title))
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setProgress(100, 0, false)
        val composeData = inputData.toComposeData()
        val accountDetails = inputData.getString("accountKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let {
            accountRepository.findByAccountKey(accountKey = it)
        }?.let {
            accountRepository.getAccountDetails(it)
        } ?: return Result.failure()
        val notificationId = composeData.draftId.hashCode()
        @Suppress("UNCHECKED_CAST")
        val service = accountDetails.service as T
        notificationManagerCompat.notify(notificationId, builder.build())

        return try {
            val mediaIds = arrayListOf<String>()
            val images = composeData.images.map {
                Uri.parse(it)
            }
            images.forEachIndexed { index, uri ->
                val id = uploadImage(uri, service)
                id?.let { mediaIds.add(it) }
                builder.setProgress(
                    100,
                    (99f * index.toFloat() / composeData.images.size.toFloat()).roundToInt(),
                    false
                )
                notificationManagerCompat.notify(notificationId, builder.build())
            }
            builder.setProgress(100, 99, false)
            notificationManagerCompat.notify(notificationId, builder.build())
            compose(service, composeData, mediaIds)
            builder.setOngoing(false)
                .setProgress(0, 0, false)
                .setSilent(false)
                .setContentTitle(applicationContext.getString(R.string.common_alerts_tweet_sent_title))
            notificationManagerCompat.notify(notificationId, builder.build())
            Result.success()
        } catch (e: Throwable) {
            e.printStackTrace()
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(Route.DeepLink.Draft(composeData.draftId)))
            val pendingIntent =
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_MUTABLE
                )
            builder.setOngoing(false)
                .setProgress(0, 0, false)
                .setSilent(false)
                .setAutoCancel(true)
                .setContentTitle(applicationContext.getString(R.string.common_alerts_tweet_fail_title))
                .setContentText(composeData.content)
                .setContentIntent(pendingIntent)
            notificationManagerCompat.notify(notificationId, builder.build())
            Result.failure()
        }
    }

    protected abstract suspend fun compose(
        service: T,
        composeData: ComposeData,
        mediaIds: ArrayList<String>
    )

    protected abstract suspend fun uploadImage(
        uri: Uri,
        service: T
    ): String?
}
