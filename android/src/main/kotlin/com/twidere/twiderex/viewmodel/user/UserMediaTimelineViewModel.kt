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
package com.twidere.twiderex.viewmodel.user

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.flatMap
import androidx.paging.map
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.ext.asStateIn
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.transform.toUi
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.paging.mediator.paging.pager
import com.twidere.twiderex.paging.mediator.user.UserMediaMediator
import com.twidere.twiderex.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class UserMediaTimelineViewModel(
    private val database: CacheDatabase,
    private val repository: AccountRepository,
    private val userKey: MicroBlogKey,
) : ViewModel() {
    private val account by lazy {
        repository.activeAccount.asStateIn(viewModelScope, null)
    }

    val source: Flow<PagingData<Pair<UiMedia, UiStatus>>> by lazy {
        pagingMediator.flatMapLatest {
            it?.let { pagingMediator ->
                pagingMediator.pager(
                    config = PagingConfig(
                        pageSize = 200,
                        prefetchDistance = 4,
                        enablePlaceholders = false,
                    )
                ).flow.map { pagingData ->
                    pagingData.map {
                        it.toUi(pagingMediator.accountKey)
                    }
                }.cachedIn(viewModelScope).map {
                    it.flatMap {
                        it.media.map { media -> media to it }
                    }
                }
            } ?: emptyFlow()
        }
    }

    val pagingMediator by lazy {
        account.map {
            it?.let {
                UserMediaMediator(
                    userKey = userKey,
                    database = database,
                    accountKey = it.accountKey,
                    service = it.service as TimelineService
                )
            }
        }.asStateIn(viewModelScope, null)
    }
}
