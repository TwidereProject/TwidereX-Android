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
package com.twidere.twiderex.action

import androidx.work.WorkManager
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.job.ComposeData
import com.twidere.twiderex.worker.compose.MastodonComposeWorker
import com.twidere.twiderex.worker.compose.TwitterComposeWorker
import com.twidere.twiderex.worker.draft.RemoveDraftWorker
import com.twidere.twiderex.worker.draft.SaveDraftWorker

actual class ComposeAction(
    private val workManager: WorkManager,
) {
    actual fun commit(
        accountKey: MicroBlogKey,
        platformType: PlatformType,
        data: ComposeData,
    ) {
        val worker = when (platformType) {
            PlatformType.Twitter -> TwitterComposeWorker.create(
                accountKey = accountKey,
                data = data,
            )
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon -> MastodonComposeWorker.create(
                accountKey = accountKey,
                data = data,
            )
        }
        workManager
            .beginWith(SaveDraftWorker.create(data = data))
            .then(worker)
            .then(RemoveDraftWorker.create(draftId = data.draftId))
            .enqueue()
    }
}
