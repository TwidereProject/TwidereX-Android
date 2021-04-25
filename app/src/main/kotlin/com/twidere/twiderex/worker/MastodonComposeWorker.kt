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
package com.twidere.twiderex.worker

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.twidere.services.mastodon.MastodonService
import com.twidere.services.mastodon.model.PostPoll
import com.twidere.services.mastodon.model.PostStatus
import com.twidere.twiderex.R
import com.twidere.twiderex.model.ComposeData
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.toComposeData
import com.twidere.twiderex.model.toWorkData
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.utils.notify
import com.twidere.twiderex.viewmodel.compose.ComposeType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File

@HiltWorker
class MastodonComposeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val accountRepository: AccountRepository,
    private val inAppNotification: InAppNotification,
) : CoroutineWorker(context, workerParams) {

    companion object {
        fun create(
            accountKey: MicroBlogKey,
            data: ComposeData,
        ) = OneTimeWorkRequestBuilder<MastodonComposeWorker>()
            .setInputData(
                Data.Builder()
                    .putAll(data.toWorkData())
                    .putString("accountKey", accountKey.toString())
                    .build()
            )
            .build()
    }

    override suspend fun doWork(): Result {
        return try {
            val composeData = inputData.toComposeData()
            val accountDetails = inputData.getString("accountKey")?.let {
                MicroBlogKey.valueOf(it)
            }?.let {
                accountRepository.findByAccountKey(accountKey = it)
            }?.let {
                accountRepository.getAccountDetails(it)
            } ?: return Result.failure()

            val service = accountDetails.service as MastodonService

            val mediaIds = arrayListOf<String>()
            composeData.images.map {
                Uri.parse(it)
            }.forEach { uri ->
                val contentResolver = applicationContext.contentResolver
                val id = contentResolver.openInputStream(uri)?.use {
                    service.upload(
                        it,
                        uri.path?.let { File(it).name }?.takeIf { it.isNotEmpty() } ?: "file"
                    )
                } ?: throw Error()
                id.id?.let {
                    mediaIds.add(it)
                }
            }
            service.compose(
                PostStatus(
                    status = composeData.content,
                    inReplyToID = if (composeData.composeType == ComposeType.Reply) composeData.statusKey?.id else null,
                    mediaIDS = mediaIds,
                    sensitive = composeData.isSensitive,
                    spoilerText = composeData.contentWarningText,
                    visibility = composeData.visibility,
                    poll = composeData.voteOptions?.let {
                        PostPoll(
                            options = composeData.voteOptions,
                            expiresIn = composeData.voteExpired?.value,
                            multiple = composeData.voteMultiple
                        )
                    }
                )
            )
            inAppNotification.show(R.string.common_alerts_tweet_sent_title)
            Result.success()
        } catch (e: Throwable) {
            e.notify(inAppNotification)
            Result.failure()
        }
    }
}
