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
package com.twidere.twiderex.worker.compose

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.db.transform.toWorkData
import com.twidere.twiderex.jobs.compose.TwitterComposeJob
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.job.ComposeData

class TwitterComposeWorker(
    context: Context,
    workerParams: WorkerParameters,
    twitterComposeJob: TwitterComposeJob,
) : ComposeWorker<TwitterService>(
    context,
    workerParams,
    twitterComposeJob
) {
    companion object {
        fun create(
            accountKey: MicroBlogKey,
            data: ComposeData,
        ) = OneTimeWorkRequestBuilder<TwitterComposeWorker>()
            .setInputData(
                Data.Builder()
                    .putAll(data.toWorkData())
                    .putString("accountKey", accountKey.toString())
                    .let { data.lat?.let { it1 -> it.putDouble("lat", it1) } ?: it }
                    .let { data.long?.let { it1 -> it.putDouble("long", it1) } ?: it }
                    .build()
            )
            .build()
    }
}
