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
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.hasKeyWithValueOfType
import androidx.work.workDataOf
import com.twidere.services.http.MicroBlogException
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.model.ComposeData
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.DraftRepository
import com.twidere.twiderex.utils.notify
import com.twidere.twiderex.viewmodel.compose.ComposeType

class TwitterComposeWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val draftRepository: DraftRepository,
    private val accountRepository: AccountRepository,
    private val inAppNotification: InAppNotification,
) : CoroutineWorker(context, workerParams) {

    companion object {
        fun create(
            accountKey: MicroBlogKey,
            data: ComposeData,
        ) = OneTimeWorkRequestBuilder<TwitterComposeWorker>()
            .setInputData(
                workDataOf(
                    "accountKey" to accountKey.toString(),
                    "draftId" to data.draftId,
                    "lat" to data.lat,
                    "long" to data.long,
                )
            )
            .build()
    }

    override suspend fun doWork(): Result {
        return try {
            val accountDetails = inputData.getString("accountKey")?.let {
                MicroBlogKey.valueOf(it)
            }?.let {
                accountRepository.findByAccountKey(accountKey = it)
            }?.let {
                accountRepository.getAccountDetails(it)
            } ?: return Result.failure()
            val draft = inputData.getString("draftId")?.let {
                draftRepository.get(it)
            } ?: return Result.failure()
            val lat = inputData.takeIf {
                it.hasKeyWithValueOfType<Double>("lat")
            }?.getDouble("lat", 0.0)
            val long = inputData.takeIf {
                it.hasKeyWithValueOfType<Double>("long")
            }?.getDouble("long", 0.0)
            val service = accountDetails.service as TwitterService
            val mediaIds = arrayListOf<String>()
            draft.media.map {
                Uri.parse(it)
            }.forEach { uri ->
                val contentResolver = applicationContext.contentResolver
                val type = contentResolver.getType(uri)
                val size = contentResolver.openFileDescriptor(uri, "r")?.statSize
                val id = contentResolver.openInputStream(uri)?.use {
                    service.uploadFile(
                        it,
                        type ?: "image/*",
                        size ?: it.available().toLong()
                    )
                } ?: throw Error()
                mediaIds.add(id)
            }
            service.update(
                draft.content,
                media_ids = mediaIds,
                in_reply_to_status_id = if (draft.composeType == ComposeType.Reply) draft.statusKey?.id else null,
                repost_status_id = if (draft.composeType == ComposeType.Quote) draft.statusKey?.id else null,
                lat = lat,
                long = long,
                exclude_reply_user_ids = draft.excludedReplyUserIds
            )
            Result.success()
        } catch (e: MicroBlogException) {
            e.notify(inAppNotification)
            Result.failure()
        } catch (e: Throwable) {
            e.notify(inAppNotification)
            Result.failure()
        }
    }
}
