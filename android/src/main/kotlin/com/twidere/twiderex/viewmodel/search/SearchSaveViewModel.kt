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

import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.repository.SearchRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SearchSaveViewModel @AssistedInject constructor(
    private val repository: SearchRepository,
    @Assisted private val account: AccountDetails,
    @Assisted private val content: String,
) : ViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(account: AccountDetails, content: String): SearchSaveViewModel
    }

    val loading = MutableStateFlow(false)

    val isSaved = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            isSaved.value = repository.get(content, account.accountKey)?.saved ?: false
        }
    }

    fun save() {
        viewModelScope.launch {
            loading.value = true
            try {
                repository.addOrUpgrade(content = content, accountKey = account.accountKey, saved = true)
                isSaved.value = true
            } catch (e: Exception) {
                isSaved.value = false
            } finally {
                loading.value = false
            }
        }
    }
}
