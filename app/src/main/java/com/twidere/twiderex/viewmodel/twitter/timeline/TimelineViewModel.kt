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
package com.twidere.twiderex.viewmodel.twitter.timeline

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.paging.mediator.PagingWithGapMediator
import com.twidere.twiderex.viewmodel.PagingViewModel

abstract class TimelineViewModel(
    private val preferences: SharedPreferences,
) : PagingViewModel() {

    abstract override val pagingMediator: PagingWithGapMediator
    abstract val savedStateKey: String
    val loadingBetween: MutableLiveData<List<String>>
        get() = pagingMediator.loadingBetween

    suspend fun loadBetween(
        max_id: String,
        since_id: String,
    ) {
        pagingMediator.loadBetween(defaultLoadCount, max_id = max_id, since_id = since_id)
    }

    fun restoreScrollState(): TimelineScrollState {
        return TimelineScrollState(
            firstVisibleItemIndex = preferences.getInt("${savedStateKey}_firstVisibleItemIndex", 0),
            firstVisibleItemScrollOffset = preferences.getInt("${savedStateKey}_firstVisibleItemScrollOffset", 0),
        )
    }

    fun saveScrollState(offset: TimelineScrollState) {
        preferences.edit {
            putInt("${savedStateKey}_firstVisibleItemIndex", offset.firstVisibleItemIndex)
            putInt("${savedStateKey}_firstVisibleItemScrollOffset", offset.firstVisibleItemScrollOffset)
        }
    }
}

data class TimelineScrollState(
    val firstVisibleItemIndex: Int = 0,
    val firstVisibleItemScrollOffset: Int = 0,
)
