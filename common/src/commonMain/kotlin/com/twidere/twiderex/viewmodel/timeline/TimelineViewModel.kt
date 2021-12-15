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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
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
        pagingMediator.mapNotNull { it }.flatMapLatest {
            it.pager().toUi()
        }.cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val loadingBetween: Flow<List<MicroBlogKey>> by lazy {
        pagingMediator.mapNotNull { it }.flatMapLatest { it.loadingBetween }
            .asStateIn(viewModelScope, emptyList())
    }

    suspend fun provideScrollState(): TimelineScrollState {
        return savedStateKey.firstOrNull()?.let {
            val firstVisibleItemIndexKey = intPreferencesKey("${it}_firstVisibleItemIndex")
            val firstVisibleItemScrollOffsetKey =
                intPreferencesKey("${it}_firstVisibleItemScrollOffset")
            dataStore.data.firstOrNull()?.let {
                val firstVisibleItemIndex = it[firstVisibleItemIndexKey] ?: 0
                val firstVisibleItemScrollOffset = it[firstVisibleItemScrollOffsetKey] ?: 0
                TimelineScrollState(
                    firstVisibleItemIndex = firstVisibleItemIndex,
                    firstVisibleItemScrollOffset = firstVisibleItemScrollOffset,
                )
            }
        } ?: TimelineScrollState.Zero
    }

    @OptIn(androidx.paging.ExperimentalPagingApi::class)
    fun loadBetween(
        maxStatusKey: MicroBlogKey,
        sinceStatueKey: MicroBlogKey,
    ) = viewModelScope.launch {
        pagingMediator.firstOrNull()?.loadBetween(
            defaultLoadCount,
            maxStatusKey = maxStatusKey,
            sinceStatusKey = sinceStatueKey
        )
    }

    suspend fun saveScrollState(offset: TimelineScrollState) {
        dataStore.edit { preferences ->
            savedStateKey.firstOrNull()?.let {
                val firstVisibleItemIndexKey = intPreferencesKey("${it}_firstVisibleItemIndex")
                val firstVisibleItemScrollOffsetKey =
                    intPreferencesKey("${it}_firstVisibleItemScrollOffset")
                preferences[firstVisibleItemIndexKey] = offset.firstVisibleItemIndex
                preferences[firstVisibleItemScrollOffsetKey] = offset.firstVisibleItemScrollOffset
            }
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
