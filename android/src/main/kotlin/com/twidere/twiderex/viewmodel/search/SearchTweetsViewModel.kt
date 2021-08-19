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
package com.twidere.twiderex.viewmodel.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.twidere.services.microblog.SearchService
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.paging.mediator.paging.pager
import com.twidere.twiderex.paging.mediator.search.SearchStatusMediator
import com.twidere.twiderex.room.db.RoomCacheDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map

class SearchTweetsViewModel @AssistedInject constructor(
    val database: RoomCacheDatabase,
    @Assisted private val account: AccountDetails,
    @Assisted keyword: String,
) : ViewModel() {
    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(account: AccountDetails, keyword: String): SearchTweetsViewModel
    }

    private val service by lazy {
        account.service as SearchService
    }

    val source by lazy {
        SearchStatusMediator(keyword, database, account.accountKey, service).pager()
            .flow.map { it.map { it.status.toUi(account.accountKey) } }.cachedIn(viewModelScope)
    }
}
