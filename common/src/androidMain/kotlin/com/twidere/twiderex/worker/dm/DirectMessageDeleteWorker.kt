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
package com.twidere.twiderex.worker.dm

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.twidere.twiderex.db.transform.toDirectMessageDeleteData
import com.twidere.twiderex.db.transform.toWorkData
import com.twidere.twiderex.jobs.dm.DirectMessageDeleteJob
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.job.DirectMessageDeleteData

class DirectMessageDeleteWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val deleteJob: DirectMessageDeleteJob
) : CoroutineWorker(
    context,
    workerParams
) {
    companion object {
        fun createWorker(deleteData: DirectMessageDeleteData) = OneTimeWorkRequestBuilder<DirectMessageDeleteWorker>()
            .setInputData(
                deleteData.toWorkData()
            )
            .build()
    }

    override suspend fun doWork(): Result {
        val deleteData = inputData.toDirectMessageDeleteData()
        val accountKey = inputData.getString("accountKey")?.let {
            MicroBlogKey.valueOf(it)
        } ?: return Result.failure()
        return try {
            deleteJob.execute(
                deleteData = deleteData,
                accountKey = accountKey
            )
            Result.success()
        } catch (e: Throwable) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
