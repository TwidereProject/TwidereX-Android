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

import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class SearchSaveViewModel(
    private val repository: SearchRepository,
    private val accountRepository: AccountRepository,
    private val content: String,
) : ViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.mapNotNull { it }
    }

    val loading = MutableStateFlow(false)

    val isSaved = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            account.firstOrNull()?.let {
                isSaved.value = repository.get(content, it.accountKey)?.saved ?: false
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            loading.value = true
            try {
                account.firstOrNull()?.let {
                    repository.addOrUpgrade(
                        content = content,
                        accountKey = it.accountKey,
                        saved = true
                    )
                    isSaved.value = true
                }
            } catch (e: Exception) {
                isSaved.value = false
            } finally {
                loading.value = false
            }
        }
    }
}
