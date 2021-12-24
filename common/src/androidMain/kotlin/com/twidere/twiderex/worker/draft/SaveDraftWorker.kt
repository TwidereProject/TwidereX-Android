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
package com.twidere.twiderex.worker.draft

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.twidere.twiderex.db.transform.toComposeData
import com.twidere.twiderex.db.transform.toWorkData
import com.twidere.twiderex.jobs.draft.SaveDraftJob
import com.twidere.twiderex.model.job.ComposeData

class SaveDraftWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val saveDraftJob: SaveDraftJob
) : CoroutineWorker(context, workerParams) {

    companion object {
        fun create(data: ComposeData) = OneTimeWorkRequestBuilder<SaveDraftWorker>()
            .setInputData(data.toWorkData())
            .build()
    }

    override suspend fun doWork(): Result {
        val data = inputData.toComposeData()
        return try {
            saveDraftJob.execute(data = data)
            Result.success()
        } catch (e: Throwable) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
