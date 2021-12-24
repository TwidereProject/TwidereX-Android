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

import com.twidere.twiderex.extensions.launchCatching
import com.twidere.twiderex.jobs.database.DeleteDbStatusJob
import com.twidere.twiderex.jobs.status.DeleteStatusJob
import com.twidere.twiderex.jobs.status.LikeStatusJob
import com.twidere.twiderex.jobs.status.MastodonVoteJob
import com.twidere.twiderex.jobs.status.RetweetStatusJob
import com.twidere.twiderex.jobs.status.UnRetweetStatusJob
import com.twidere.twiderex.jobs.status.UnlikeStatusJob
import com.twidere.twiderex.jobs.status.UpdateStatusJob
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ui.UiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

actual class StatusActions(
    private val deleteStatusJob: DeleteStatusJob,
    private val deleteDbStatusJob: DeleteDbStatusJob,
    private val updateStatusJob: UpdateStatusJob,
    private val likeStatusJob: LikeStatusJob,
    private val unlikeStatusJob: UnlikeStatusJob,
    private val retweetStatusJob: RetweetStatusJob,
    private val unRetweetStatusJob: UnRetweetStatusJob,
    private val mastodonVoteJob: MastodonVoteJob,
) : IStatusActions {
    private val scope = CoroutineScope(Dispatchers.IO)

    actual override fun delete(status: UiStatus, account: AccountDetails) {
        scope.launchCatching {
            deleteStatusJob.execute(accountKey = account.accountKey, statusKey = status.statusKey)
            deleteDbStatusJob.execute(statusKey = status.statusKey)
        }
    }

    actual override fun like(status: UiStatus, account: AccountDetails) {
        scope.launchCatching {
            updateStatusJob.execute(
                accountKey = account.accountKey,
                statusKey = status.statusKey,
                liked = !status.liked
            )
            val (
                _,
                _,
                retweeted,
                liked,
                retweetCount,
                likeCount,
            ) = if (status.liked) {
                unlikeStatusJob.execute(
                    accountKey = account.accountKey,
                    statusKey = status.statusKey,
                )
            } else {
                likeStatusJob.execute(
                    accountKey = account.accountKey,
                    statusKey = status.statusKey,
                )
            }
            updateStatusJob.execute(
                accountKey = account.accountKey,
                statusKey = status.statusKey,
                liked = liked,
                likeCount = likeCount,
                retweeted = retweeted,
                retweetCount = retweetCount,
            )
        }
    }

    actual override fun retweet(status: UiStatus, account: AccountDetails) {
        scope.launchCatching {
            updateStatusJob.execute(
                accountKey = account.accountKey,
                statusKey = status.statusKey,
                retweeted = !status.retweeted
            )
            val (
                _,
                _,
                retweeted,
                liked,
                retweetCount,
                likeCount,
            ) = if (status.retweeted) {
                unRetweetStatusJob.execute(
                    accountKey = account.accountKey,
                    statusKey = status.statusKey,
                )
            } else {
                retweetStatusJob.execute(
                    accountKey = account.accountKey,
                    statusKey = status.statusKey,
                )
            }
            updateStatusJob.execute(
                accountKey = account.accountKey,
                statusKey = status.statusKey,
                liked = liked,
                likeCount = likeCount,
                retweeted = retweeted,
                retweetCount = retweetCount,
            )
        }
    }

    actual override fun vote(status: UiStatus, account: AccountDetails, votes: List<Int>) {
        scope.launchCatching {
            mastodonVoteJob.execute(votes, account.accountKey, status.statusKey)
        }
    }
}
