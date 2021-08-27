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
package com.twidere.twiderex.viewmodel.settings

import com.twidere.twiderex.extensions.asStateIn
import com.twidere.twiderex.repository.AccountRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class AccountNotificationViewModel(
    private val accountRepository: AccountRepository,
) : ViewModel() {
    val account by lazy {
        accountRepository.activeAccount.asStateIn(viewModelScope, null)
    }

    val preferences by lazy {
        account.map {
            it?.let {
                accountRepository.getAccountPreferences(it.accountKey)
            }
        }.asStateIn(viewModelScope, null)
    }

    val isNotificationEnabled by lazy {
        preferences.flatMapLatest { it?.isNotificationEnabled ?: flowOf(false) }
            .asStateIn(viewModelScope, false)
    }

    fun setIsNotificationEnabled(value: Boolean) = viewModelScope.launch {
        preferences.lastOrNull()?.setIsNotificationEnabled(value)
    }
}
