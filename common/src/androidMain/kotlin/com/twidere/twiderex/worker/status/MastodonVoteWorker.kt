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
package com.twidere.twiderex.worker.status

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.twidere.twiderex.jobs.status.MastodonVoteJob
import com.twidere.twiderex.model.MicroBlogKey

class MastodonVoteWorker(
    appContext: Context,
    params: WorkerParameters,
    private val mastodonVoteJob: MastodonVoteJob
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
        val statusKey = inputData.getString("statusKey")?.let {
            MicroBlogKey.valueOf(it)
        } ?: return Result.failure()
        return try {
            mastodonVoteJob.execute(
                votes = votes,
                accountKey = accountKey,
                statusKey = statusKey
            )
            Result.success()
        } catch (e: Throwable) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
