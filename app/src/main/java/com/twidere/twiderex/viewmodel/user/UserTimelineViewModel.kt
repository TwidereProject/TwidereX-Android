/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.viewmodel.user

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.repository.UserRepository

class UserTimelineViewModel @ViewModelInject constructor(private val repository: UserRepository) :
    UserTimelineViewModelBase() {
    override val source: LiveData<List<UiStatus>>
        get() = repository.getUserTimelineLiveData(timelineType = TimelineType.User)

    override suspend fun loadBetween(
        user: UiUser,
        max_id: String?,
        since_Id: String?
    ): List<UiStatus> {
        // TODO: pinned tweets
//        val pinned = repository.getPinnedStatus(user)
//        if (pinned != null) {
//            timelineIds.add(pinned.statusId)
//        }
        return repository.loadTimelineBetween(
            user.id,
            max_id = max_id,
            since_id = since_Id,
        )
    }
}
