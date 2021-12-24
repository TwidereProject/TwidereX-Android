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
import com.twidere.services.mastodon.MastodonService
import com.twidere.twiderex.db.transform.toWorkData
import com.twidere.twiderex.jobs.compose.MastodonComposeJob
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.job.ComposeData

class MastodonComposeWorker(
    context: Context,
    workerParams: WorkerParameters,
    mastodonComposeJob: MastodonComposeJob
) : ComposeWorker<MastodonService>(context, workerParams, mastodonComposeJob) {

    companion object {
        fun create(
            accountKey: MicroBlogKey,
            data: ComposeData,
        ) = OneTimeWorkRequestBuilder<MastodonComposeWorker>()
            .setInputData(
                Data.Builder()
                    .putAll(data.toWorkData())
                    .putString("accountKey", accountKey.toString())
                    .build()
            )
            .build()
    }
}
