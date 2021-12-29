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

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.twidere.twiderex.model.ui.UiSearch
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class SearchInputViewModel(
    private val repository: SearchRepository,
    private val accountRepository: AccountRepository,
    keyword: String
) : ViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.mapNotNull { it }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val source by lazy {
        account.flatMapLatest {
            repository.searchHistory(it.accountKey)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val savedSource by lazy {
        account.flatMapLatest {
            repository.savedSearch(it.accountKey)
        }
    }

    private val _searchInput = MutableStateFlow(TextFieldValue(keyword, TextRange(keyword.length)))
    val searchInput = _searchInput.asStateFlow()

    fun updateSearchInput(searchInput: TextFieldValue) {
        _searchInput.value = searchInput
    }

    val expandSearch = MutableStateFlow(false)

    fun remove(item: UiSearch) = viewModelScope.launch {
        repository.remove(item)
    }

    fun addOrUpgrade(content: String) = viewModelScope.launch {
        account.firstOrNull()?.let {
            repository.addOrUpgrade(content, it.accountKey)
        }
    }
}
