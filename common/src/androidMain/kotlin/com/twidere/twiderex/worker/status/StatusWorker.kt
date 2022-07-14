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
import com.twidere.twiderex.db.transform.toWorkData
import com.twidere.twiderex.jobs.status.StatusJob
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus

abstract class StatusWorker(
    appContext: Context,
    params: WorkerParameters,
    private val statusJob: StatusJob,
) : CoroutineWorker(appContext, params) {
    companion object {
        inline fun <reified T : StatusWorker> create(
            accountKey: MicroBlogKey,
            status: UiStatus,
        ) = OneTimeWorkRequestBuilder<T>()
            .setInputData(
                workDataOf(
                    "accountKey" to accountKey.toString(),
                    "statusKey" to status.statusKey.toString(),
                )
            )
            .build()
    }

    override suspend fun doWork(): Result {
        val accountKey = inputData.getString("accountKey")?.let {
            MicroBlogKey.valueOf(it)
        } ?: return Result.failure()
        val statusKey = inputData.getString("statusKey")?.let {
            MicroBlogKey.valueOf(it)
        } ?: return Result.failure()
        return try {
            statusJob.execute(
                accountKey = accountKey,
                statusKey = statusKey
            ).let {
                Result.success(it.toWorkData())
            }
        } catch (e: Throwable) {
            Result.failure()
        }
    }
}
