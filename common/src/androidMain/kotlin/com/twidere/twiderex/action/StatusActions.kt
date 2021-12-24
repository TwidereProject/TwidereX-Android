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
package com.twidere.twiderex.action

import androidx.work.WorkManager
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.job.StatusResult
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.worker.database.DeleteDbStatusWorker
import com.twidere.twiderex.worker.status.DeleteStatusWorker
import com.twidere.twiderex.worker.status.LikeWorker
import com.twidere.twiderex.worker.status.MastodonVoteWorker
import com.twidere.twiderex.worker.status.RetweetWorker
import com.twidere.twiderex.worker.status.StatusWorker
import com.twidere.twiderex.worker.status.UnLikeWorker
import com.twidere.twiderex.worker.status.UnRetweetWorker
import com.twidere.twiderex.worker.status.UpdateStatusWorker

actual class StatusActions(
    private val workManager: WorkManager,
) : IStatusActions {
    actual override fun delete(status: UiStatus, account: AccountDetails) {
        workManager.beginWith(
            DeleteStatusWorker.create(
                status = status,
                accountKey = account.accountKey
            )
        ).then(DeleteDbStatusWorker.create(status.statusKey))
            .enqueue()
    }

    actual override fun like(status: UiStatus, account: AccountDetails) {
        workManager.beginWith(
            UpdateStatusWorker.create(
                StatusResult(
                    accountKey = account.accountKey,
                    statusKey = status.statusKey,
                    liked = !status.liked
                )
            )
        ).then(
            if (status.liked) {
                StatusWorker.create<UnLikeWorker>(
                    accountKey = account.accountKey,
                    status = status
                )
            } else {
                StatusWorker.create<LikeWorker>(
                    accountKey = account.accountKey,
                    status = status
                )
            }
        ).then(listOf(UpdateStatusWorker.create())).enqueue()
    }

    actual override fun retweet(status: UiStatus, account: AccountDetails) {
        workManager.beginWith(
            UpdateStatusWorker.create(
                StatusResult(
                    accountKey = account.accountKey,
                    statusKey = status.statusKey,
                    retweeted = !status.retweeted
                )
            )
        ).then(
            if (status.retweeted) {
                StatusWorker.create<UnRetweetWorker>(
                    accountKey = account.accountKey,
                    status = status
                )
            } else {
                StatusWorker.create<RetweetWorker>(
                    accountKey = account.accountKey,
                    status = status
                )
            }
        ).then(listOf(UpdateStatusWorker.create())).enqueue()
    }

    actual override fun vote(status: UiStatus, account: AccountDetails, votes: List<Int>) {
        workManager.beginWith(
            MastodonVoteWorker.create(
                statusKey = status.statusKey,
                accountKey = account.accountKey,
                votes = votes
            )
        ).enqueue()
    }
}
