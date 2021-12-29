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
package com.twidere.twiderex.viewmodel.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.twidere.services.microblog.SearchService
import com.twidere.twiderex.maxSearchUserCount
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.paging.source.SearchUserPagingSource
import com.twidere.twiderex.repository.AccountRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class SearchUserViewModel(
    private val accountRepository: AccountRepository,
    keyword: String,
    following: Boolean = false,
) : ViewModel() {
    private val account: Flow<AccountDetails> by lazy {
        accountRepository.activeAccount.mapNotNull { it }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val source: Flow<PagingData<UiUser>> by lazy {
        account.flatMapLatest { account ->
            Pager(
                config = PagingConfig(
                    pageSize = maxSearchUserCount,
                    initialLoadSize = maxSearchUserCount,
                    enablePlaceholders = false,
                )
            ) {
                SearchUserPagingSource(
                    accountKey = account.accountKey,
                    keyword,
                    account.service as SearchService,
                    following = following
                )
            }.flow
        }.cachedIn(viewModelScope)
    }
}
