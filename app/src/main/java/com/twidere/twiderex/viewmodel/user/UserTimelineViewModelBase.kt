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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiUser

abstract class UserTimelineViewModelBase : ViewModel() {
    private val timelineIds = arrayListOf<String>()
    private var hasMore = true
    val loadingMore = MutableLiveData(false)
    val timeline = liveData {
        emitSource(
            source.switchMap { result ->
                liveData {
                    emit(
                        timelineIds.mapNotNull { id ->
                            result.firstOrNull { it.statusId == id }
                        }
                    )
                }
            }
        )
    }
    abstract val source: LiveData<List<UiStatus>>

    fun clear() {
        timelineIds.clear()
    }

    suspend fun refresh(user: UiUser) {
        if (!timelineIds.isNullOrEmpty()) {
            timelineIds.clear()
        }
        loadingMore.postValue(true)
        val result = loadBetween(user)
        timelineIds.addAll(result.map { it.statusId }.filter { !timelineIds.contains(it) })
        loadingMore.postValue(false)
    }

    suspend fun loadMore(user: UiUser) {
        if (!hasMore) {
            return
        }
        loadingMore.postValue(true)
        val result = loadBetween(user, max_id = timelineIds.lastOrNull())
        hasMore = result.any()
        timelineIds.addAll(result.map { it.statusId }.filter { !timelineIds.contains(it) })
        loadingMore.postValue(false)
    }

    protected abstract suspend fun loadBetween(
        user: UiUser,
        max_id: String? = null,
        since_Id: String? = null,
    ): List<UiStatus>
}
