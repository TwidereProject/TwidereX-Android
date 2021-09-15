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
package com.twidere.twiderex.viewmodel.timeline

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.paging.cachedIn
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.extensions.asStateIn
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.paging.mediator.paging.PagingWithGapMediator
import com.twidere.twiderex.paging.mediator.paging.pager
import com.twidere.twiderex.paging.mediator.paging.toUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

abstract class TimelineViewModel(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    abstract val pagingMediator: Flow<PagingWithGapMediator?>
    abstract val savedStateKey: Flow<String?>

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val source by lazy {
        pagingMediator.transformLatest {
            it?.let {
                emitAll(it.pager().toUi())
            }
        }.cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val loadingBetween: Flow<List<MicroBlogKey>> by lazy {
        pagingMediator.flatMapLatest { it?.loadingBetween ?: emptyFlow() }
            .asStateIn(viewModelScope, emptyList())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val timelineScrollState by lazy {
        savedStateKey.flatMapLatest {
            val firstVisibleItemIndexKey = intPreferencesKey("${it}_firstVisibleItemIndex")
            val firstVisibleItemScrollOffsetKey =
                intPreferencesKey("${it}_firstVisibleItemScrollOffset")
            dataStore.data.map {
                val firstVisibleItemIndex = it[firstVisibleItemIndexKey] ?: 0
                val firstVisibleItemScrollOffset = it[firstVisibleItemScrollOffsetKey] ?: 0
                TimelineScrollState(
                    firstVisibleItemIndex = firstVisibleItemIndex,
                    firstVisibleItemScrollOffset = firstVisibleItemScrollOffset,
                )
            }
        }.asStateIn(viewModelScope, TimelineScrollState.Zero)
    }

    fun loadBetween(
        maxStatusKey: MicroBlogKey,
        sinceStatueKey: MicroBlogKey,
    ) = viewModelScope.launch {
        pagingMediator.lastOrNull()?.loadBetween(
            defaultLoadCount,
            maxStatusKey = maxStatusKey,
            sinceStatusKey = sinceStatueKey
        )
    }

    fun saveScrollState(offset: TimelineScrollState) = viewModelScope.launch {
        dataStore.edit {
            val firstVisibleItemIndexKey = intPreferencesKey("${it}_firstVisibleItemIndex")
            val firstVisibleItemScrollOffsetKey =
                intPreferencesKey("${it}_firstVisibleItemScrollOffset")
            it[firstVisibleItemIndexKey] = offset.firstVisibleItemIndex
            it[firstVisibleItemScrollOffsetKey] = offset.firstVisibleItemScrollOffset
        }
    }
}

data class TimelineScrollState(
    val firstVisibleItemIndex: Int = 0,
    val firstVisibleItemScrollOffset: Int = 0,
) {
    companion object {
        val Zero = TimelineScrollState(0, 0)
    }
}
