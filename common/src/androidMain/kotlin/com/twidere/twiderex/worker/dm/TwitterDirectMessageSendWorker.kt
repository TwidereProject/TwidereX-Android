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
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.twidere.twiderex.db.transform.toWorkData
import com.twidere.twiderex.jobs.dm.TwitterDirectMessageSendJob
import com.twidere.twiderex.model.job.DirectMessageSendData

class TwitterDirectMessageSendWorker(
    context: Context,
    workerParams: WorkerParameters,
    twitterDirectMessageSendJob: TwitterDirectMessageSendJob
) : DirectMessageSendWorker(
    context,
    workerParams,
    twitterDirectMessageSendJob
) {
    companion object {
        fun create(
            data: DirectMessageSendData,
        ) = OneTimeWorkRequestBuilder<TwitterDirectMessageSendWorker>()
            .setInputData(
                Data.Builder()
                    .putAll(data.toWorkData())
                    .build()
            )
            .build()
    }
}
