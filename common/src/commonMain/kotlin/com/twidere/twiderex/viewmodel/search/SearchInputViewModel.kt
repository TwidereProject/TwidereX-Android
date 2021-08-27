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

import com.twidere.twiderex.extensions.asStateIn
import com.twidere.twiderex.model.ui.UiSearch
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class SearchInputViewModel(
    private val repository: SearchRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.asStateIn(viewModelScope, null)
    }

    val source by lazy {
        account.flatMapLatest {
            it?.let {
                repository.searchHistory(it.accountKey)
            } ?: flowOf(emptyList())
        }
    }

    val savedSource by lazy {
        account.flatMapLatest {
            it?.let {
                repository.savedSearch(it.accountKey)
            } ?: flowOf(emptyList())
        }
    }

    val expandSearch = MutableStateFlow(false)

    fun remove(item: UiSearch) = viewModelScope.launch {
        repository.remove(item)
    }

    fun addOrUpgrade(content: String) = viewModelScope.launch {
        account.lastOrNull()?.let {
            repository.addOrUpgrade(content, it.accountKey)
        }
    }
}
