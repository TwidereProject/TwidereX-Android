/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.WorkerParameters
import com.twidere.services.microblog.StatusService
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.StatusRepository

class UnRetweetWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    accountRepository: AccountRepository,
    statusRepository: StatusRepository
) : StatusWorker(appContext, params, accountRepository, statusRepository) {
    override suspend fun doWork(
        accountKey: MicroBlogKey,
        service: StatusService,
        status: UiStatus
    ): StatusResult {
        val newStatus = service.unRetweet(status.statusId)
            .toDbTimeline(accountKey = accountKey, timelineType = TimelineType.Custom)
            .toUi(accountKey = accountKey)
        return StatusResult(
            statusKey = status.statusKey,
            accountKey = accountKey,
            retweeted = false,
            retweetCount = newStatus.retweetCount,
            likeCount = newStatus.likeCount,
        )
    }
    override fun fallback(
        accountKey: MicroBlogKey,
        status: UiStatus,
    ) = StatusResult(
        accountKey = accountKey,
        statusKey = status.statusKey,
        retweeted = true,
    )
}
