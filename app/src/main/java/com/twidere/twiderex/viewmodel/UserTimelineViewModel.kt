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
 
package com.twidere.twiderex.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.repository.UserRepository

class UserTimelineViewModel @ViewModelInject constructor(
    private val repository: UserRepository,
) : ViewModel() {
    val loadingMore = MutableLiveData(false)
    val timeline = liveData {
        emitSource(
            repository.getUserTimelineLiveData().switchMap {
                liveData {
                    emit(
                        it.filter {
                            timelineIds.contains(it.statusId)
                        }
                    )
                }
            }
        )
    }
    private val timelineIds = arrayListOf<String>()

    suspend fun refresh(user: UiUser) {
        if (!timelineIds.isNullOrEmpty()) {
            return
        }
        loadingMore.postValue(true)
        repository.loadTimelineBetween(
            user.id,
        ).map { it.statusId }.let {
            timelineIds.addAll(it)
        }
        loadingMore.postValue(false)
    }

    suspend fun loadMore(user: UiUser) {
        loadingMore.postValue(true)
        val result = repository.loadTimelineBetween(
            user.id,
            max_id = timelineIds.lastOrNull()
        )
        timelineIds.addAll(result.map { it.statusId })
        loadingMore.postValue(false)
    }
}
