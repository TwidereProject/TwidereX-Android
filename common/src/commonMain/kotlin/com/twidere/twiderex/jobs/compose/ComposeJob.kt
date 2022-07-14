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
package com.twidere.twiderex.jobs.compose

import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.kmp.ExifScrambler
import com.twidere.twiderex.kmp.RemoteNavigator
import com.twidere.twiderex.kmp.ResLoader
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.job.ComposeData
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.navigation.RootDeepLinks
import com.twidere.twiderex.notification.AppNotification
import com.twidere.twiderex.notification.AppNotificationManager
import com.twidere.twiderex.notification.NotificationChannelSpec
import com.twidere.twiderex.repository.AccountRepository
import kotlin.math.roundToInt

abstract class ComposeJob<T : MicroBlogService>(
    private val accountRepository: AccountRepository,
    private val notificationManager: AppNotificationManager,
    private val exifScrambler: ExifScrambler,
    private val remoteNavigator: RemoteNavigator,
    private val resLoader: ResLoader,
) {
    @OptIn(kotlin.time.ExperimentalTime::class)
    suspend fun execute(composeData: ComposeData, accountKey: MicroBlogKey) {
        val builder = AppNotification
            .Builder(NotificationChannelSpec.BackgroundProgresses.id)
            .setContentTitle(resLoader.getString(com.twidere.twiderex.MR.strings.common_alerts_tweet_sending_title))
            .setOngoing(true)
            .setSilent(true)
            .setProgress(100, 0, false)
        val accountDetails = accountKey.let {
            accountRepository.findByAccountKey(accountKey = it)
        } ?: throw Error("Can't find any account matches:$$accountKey")
        val notificationId = composeData.draftId.hashCode()

        @Suppress("UNCHECKED_CAST")
        val service = accountDetails.service as T
        notificationManager.notify(notificationId, builder.build())

        try {
            val mediaIds = arrayListOf<String>()
            val images = composeData.images
            images.forEachIndexed { index, uri ->
                val scramblerUri = exifScrambler.removeExifData(uri, imageMaxSize)
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
            val status = compose(service, composeData, accountKey, mediaIds)
            builder.setOngoing(false)
                .setProgress(0, 0, false)
                .setSilent(false)
                .setContentTitle(resLoader.getString(com.twidere.twiderex.MR.strings.common_alerts_tweet_sent_title))
            notificationManager.notifyTransient(notificationId, builder.build())
            if (composeData.isThreadMode) {
                // open compose scene in thread mode
                remoteNavigator.openDeepLink(
                    deeplink = RootDeepLinks.Compose(ComposeType.Thread, status.statusKey),
                    fromBackground = true
                )
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            builder.setOngoing(false)
                .setProgress(0, 0, false)
                .setSilent(false)
                .setContentTitle(resLoader.getString(com.twidere.twiderex.MR.strings.common_alerts_tweet_fail_title))
                .setContentText(composeData.content)
                .setDeepLink(RootDeepLinks.Draft(composeData.draftId))
            notificationManager.notify(notificationId, builder.build())
            throw e
        }
    }

    protected abstract suspend fun compose(
        service: T,
        composeData: ComposeData,
        accountKey: MicroBlogKey,
        mediaIds: ArrayList<String>
    ): UiStatus

    protected abstract suspend fun uploadImage(
        originUri: String,
        scramblerUri: String,
        service: T
    ): String?

    protected abstract val imageMaxSize: Long
}
