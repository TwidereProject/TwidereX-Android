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
package com.twidere.twiderex.worker.dm

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.twidere.twiderex.extensions.toWorkResult
import com.twidere.twiderex.jobs.dm.DirectMessageSendJob
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.toDirectMessageSendData

abstract class DirectMessageSendWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val directMessageSendJob: DirectMessageSendJob<*>,
) : CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {
        val sendData = inputData.toDirectMessageSendData()
        val accountKey = inputData.getString("accountKey")?.let {
            MicroBlogKey.valueOf(it)
        } ?: return Result.failure()
        return directMessageSendJob.execute(
            sendData = sendData,
            accountKey = accountKey
        ).toWorkResult()
    }
}
