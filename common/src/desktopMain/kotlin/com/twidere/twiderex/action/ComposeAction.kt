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
import com.twidere.twiderex.jobs.compose.MastodonComposeJob
import com.twidere.twiderex.jobs.compose.TwitterComposeJob
import com.twidere.twiderex.jobs.draft.RemoveDraftJob
import com.twidere.twiderex.jobs.draft.SaveDraftJob
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.job.ComposeData
import com.twidere.twiderex.repository.AccountRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull

actual class ComposeAction(
    private val repository: AccountRepository,
    private val saveDraftJob: SaveDraftJob,
    private val removeDraftJob: RemoveDraftJob,
    private val twitterComposeJob: TwitterComposeJob,
    private val mastodonComposeJob: MastodonComposeJob,
) {

    private val scope = CoroutineScope(Dispatchers.IO)

    actual fun commit(
        data: ComposeData,
    ) {
        scope.launchCatching {
            repository.activeAccount.firstOrNull()?.toUi()?.let { account ->
                val platformType = account.platformType
                val accountKey = account.userKey
                saveDraftJob.execute(data)
                when (platformType) {
                    PlatformType.Twitter -> twitterComposeJob.execute(data, accountKey)
                    PlatformType.StatusNet -> TODO()
                    PlatformType.Fanfou -> TODO()
                    PlatformType.Mastodon -> mastodonComposeJob.execute(data, accountKey)
                }
                removeDraftJob.execute(data.draftId)
            }
        }
    }
}
