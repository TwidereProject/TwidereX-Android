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
package com.twidere.twiderex.worker.status

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.twidere.services.mastodon.MastodonService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.StatusRepository
import com.twidere.twiderex.utils.notify
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MastodonVoteWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val accountRepository: AccountRepository,
    private val statusRepository: StatusRepository,
    private val inAppNotification: InAppNotification,
) : CoroutineWorker(appContext, params) {

    companion object {
        fun create(
            statusKey: MicroBlogKey,
            accountKey: MicroBlogKey,
            votes: List<Int>
        ) = OneTimeWorkRequestBuilder<MastodonVoteWorker>()
            .setInputData(
                workDataOf(
                    "statusKey" to statusKey.toString(),
                    "accountKey" to accountKey.toString(),
                    "votes" to votes.toTypedArray(),
                )
            ).build()
    }

    override suspend fun doWork(): Result {
        val votes = (inputData.getIntArray("votes") ?: intArrayOf()).toList()
        if (votes.isEmpty()) {
            return Result.success()
        }
        val accountKey = inputData.getString("accountKey")?.let {
            MicroBlogKey.valueOf(it)
        } ?: return Result.failure()
        val status = inputData.getString("statusKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let {
            statusRepository.loadFromCache(it, accountKey = accountKey)
        } ?: return Result.failure()
        if (status.mastodonExtra?.poll == null || status.platformType != PlatformType.Mastodon) {
            return Result.success()
        }
        val service = accountRepository.findByAccountKey(accountKey)?.let {
            accountRepository.getAccountDetails(it)
        }?.let {
            it.service as? MastodonService
        } ?: return Result.failure()

        val pollId = status.mastodonExtra.poll.id ?: return Result.failure()
        val originPoll = status.mastodonExtra.poll
        statusRepository.updateStatus(statusKey = status.statusKey) {
            it.mastodonExtra = status.mastodonExtra.copy(
                poll = status.mastodonExtra.poll.copy(
                    voted = true,
                    ownVotes = votes
                )
            )
        }
        return try {
            val newPoll = service.vote(pollId, votes)
            statusRepository.updateStatus(statusKey = status.statusKey) {
                it.mastodonExtra = status.mastodonExtra.copy(
                    poll = newPoll
                )
            }
            Result.success()
        } catch (e: Throwable) {
            statusRepository.updateStatus(statusKey = status.statusKey) {
                it.mastodonExtra = status.mastodonExtra.copy(
                    poll = originPoll
                )
            }
            e.notify(inAppNotification)
            Result.failure()
        }
    }
}
