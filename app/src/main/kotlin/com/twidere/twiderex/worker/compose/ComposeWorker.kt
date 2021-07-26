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

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.R
import com.twidere.twiderex.model.ComposeData
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.toComposeData
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.navigation.RootRoute
import com.twidere.twiderex.notification.AppNotification
import com.twidere.twiderex.notification.AppNotificationManager
import com.twidere.twiderex.notification.NotificationChannelSpec
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.utils.ExifScrambler
import com.twidere.twiderex.viewmodel.compose.ComposeType
import kotlin.math.roundToInt

abstract class ComposeWorker<T : MicroBlogService>(
    protected val context: Context,
    workerParams: WorkerParameters,
    private val accountRepository: AccountRepository,
    private val notificationManager: AppNotificationManager,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val builder = AppNotification
            .Builder(NotificationChannelSpec.BackgroundProgresses.id)
            .setContentTitle(applicationContext.getString(R.string.common_alerts_tweet_sending_title))
            .setOngoing(true)
            .setSilent(true)
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
        notificationManager.notify(notificationId, builder.build())

        return try {
            val exifScrambler = ExifScrambler(context)
            val mediaIds = arrayListOf<String>()
            val images = composeData.images.map {
                Uri.parse(it)
            }
            images.forEachIndexed { index, uri ->
                val scramblerUri = exifScrambler.removeExifData(uri)
                val id = uploadImage(uri, scramblerUri, service)
                id?.let { mediaIds.add(it) }
                builder.setProgress(
                    100,
                    (99f * index.toFloat() / composeData.images.size.toFloat()).roundToInt(),
                    false
                )
                notificationManager.notify(notificationId, builder.build())
                exifScrambler.deleteCacheFile(scramblerUri)
            }
            builder.setProgress(100, 99, false)
            notificationManager.notify(notificationId, builder.build())
            val status = compose(service, composeData, mediaIds)
            builder.setOngoing(false)
                .setProgress(0, 0, false)
                .setSilent(false)
                .setContentTitle(applicationContext.getString(R.string.common_alerts_tweet_sent_title))
            notificationManager.notifyTransient(notificationId, builder.build())
            if (composeData.isThreadMode) {
                // open compose scene in thread mode
                applicationContext.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(RootRoute.DeepLink.Compose(ComposeType.Thread, status.statusKey))
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
            }
            Result.success()
        } catch (e: Throwable) {
            e.printStackTrace()
            builder.setOngoing(false)
                .setProgress(0, 0, false)
                .setSilent(false)
                .setContentTitle(applicationContext.getString(R.string.common_alerts_tweet_fail_title))
                .setContentText(composeData.content)
                .setDeepLink(RootRoute.DeepLink.Draft(composeData.draftId))
            notificationManager.notify(notificationId, builder.build())
            Result.failure()
        }
    }

    protected abstract suspend fun compose(
        service: T,
        composeData: ComposeData,
        mediaIds: ArrayList<String>
    ): UiStatus

    protected abstract suspend fun uploadImage(
        originUri: Uri,
        scramblerUri: Uri,
        service: T
    ): String?
}
