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
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.twidere.twiderex.jobs.dm.DirectMessageFetchJob
import java.util.concurrent.TimeUnit

class DirectMessageFetchWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val directMessageFetchJob: DirectMessageFetchJob
) : CoroutineWorker(
    context,
    workerParams
) {
    companion object {
        fun createRepeatableWorker() = PeriodicWorkRequestBuilder<DirectMessageFetchWorker>(15, TimeUnit.MINUTES)
            .build()
    }

    override suspend fun doWork(): Result {
        try {
            directMessageFetchJob.execute()
        } catch (e: Throwable) {
            // no need to handle this error
        }
        return Result.success()
    }
}
