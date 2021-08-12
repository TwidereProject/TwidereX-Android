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
package com.twidere.twiderex.jobs.status

import com.twidere.services.mastodon.MastodonService
import com.twidere.services.mastodon.model.Poll
import com.twidere.twiderex.db.model.DbMastodonStatusExtra
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.StatusRepository
import com.twidere.twiderex.utils.fromJson
import com.twidere.twiderex.utils.json
import com.twidere.twiderex.utils.notify

class MastodonVoteJob(
    private val accountRepository: AccountRepository,
    private val statusRepository: StatusRepository,
    private val inAppNotification: InAppNotification,
) {
    suspend fun execute(votes: List<Int>, accountKey: MicroBlogKey, statusKey: MicroBlogKey) {
        val status = statusRepository.loadFromCache(statusKey, accountKey = accountKey) ?: throw Error("Can't find any status matches:$statusKey")
        if (status.poll == null || status.platformType != PlatformType.Mastodon) {
            throw Error()
        }
        val service = accountRepository.findByAccountKey(accountKey)?.let {
            accountRepository.getAccountDetails(it)
        }?.let {
            it.service as? MastodonService
        } ?: throw Error()

        val pollId = status.poll.id
        var originPoll: Poll? = null
        statusRepository.updateStatus(statusKey = status.statusKey, accountKey = accountKey) {
            it.extra = it.extra.fromJson<DbMastodonStatusExtra>()
                .let { extra ->
                    originPoll = extra.poll
                    extra.copy(
                        poll = extra.poll?.copy(
                            voted = true,
                            ownVotes = votes
                        )
                    )
                }.json()
        }
        try {
            val newPoll = service.vote(pollId, votes)
            statusRepository.updateStatus(statusKey = status.statusKey, accountKey = accountKey) {
                it.extra = it.extra.fromJson<DbMastodonStatusExtra>().copy(
                    poll = newPoll
                ).json()
            }
        } catch (e: Throwable) {
            statusRepository.updateStatus(statusKey = status.statusKey, accountKey = accountKey) {
                it.extra = it.extra.fromJson<DbMastodonStatusExtra>().copy(
                    poll = originPoll
                ).json()
            }
            e.notify(inAppNotification)
            throw e
        }
    }
}
