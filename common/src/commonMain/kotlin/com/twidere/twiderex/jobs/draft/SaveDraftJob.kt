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
package com.twidere.twiderex.jobs.draft

import com.twidere.twiderex.model.job.ComposeData
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.DraftRepository
import com.twidere.twiderex.utils.notifyError

class SaveDraftJob(
    private val repository: DraftRepository,
    private val inAppNotification: InAppNotification,
) {
    suspend fun execute(data: ComposeData) {
        with(data) {
            try {
                repository.addOrUpgrade(
                    content,
                    images,
                    composeType = composeType,
                    statusKey = statusKey,
                    draftId = draftId,
                    excludedReplyUserIds = excludedReplyUserIds,
                )
            } catch (e: Throwable) {
                inAppNotification.notifyError(e)
                throw e
            }
        }
    }
}
