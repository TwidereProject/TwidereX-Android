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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.paging.mediator.PagingWithGapMediator
import com.twidere.twiderex.paging.mediator.pager
import kotlinx.coroutines.flow.map

abstract class TimelineViewModel(
    private val preferences: SharedPreferences,
) : ViewModel() {

    abstract val pagingMediator: PagingWithGapMediator
    abstract val savedStateKey: String

    val source by lazy {
        pagingMediator.pager().flow.map {
            it.map {
                it.toUi(pagingMediator.userKey)
            }
        }.cachedIn(viewModelScope)
    }

    val loadingBetween = MutableLiveData(listOf<String>())

    suspend fun loadBetween(
        max_id: String,
        since_id: String,
    ) {
        loadingBetween.postValue((loadingBetween.value ?: listOf()) + max_id)
        runCatching {
            pagingMediator.loadBetween(defaultLoadCount, max_id = max_id, since_id = since_id)
        }.onFailure {

        }
        loadingBetween.postValue((loadingBetween.value ?: listOf()) - max_id)
    }

    fun restoreScrollState(): Int {
        return preferences.getInt("${savedStateKey}_offset", 0)
    }

    fun saveScrollState(offset: Int) {
        preferences.edit {
            putInt("${savedStateKey}_offset", offset)
        }
    }
}
