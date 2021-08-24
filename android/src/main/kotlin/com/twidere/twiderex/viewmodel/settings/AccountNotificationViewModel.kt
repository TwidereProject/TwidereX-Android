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

import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.repository.AccountRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AccountNotificationViewModel @AssistedInject constructor(
    private val accountRepository: AccountRepository,
    @Assisted accountKey: MicroBlogKey,
) : ViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            accountKey: MicroBlogKey,
        ): AccountNotificationViewModel
    }

    val account by lazy {
        accountRepository.accounts.map {
            it.firstOrNull { it.accountKey == accountKey }?.toUi()
        }
    }

    val preferences by lazy {
        accountRepository.getAccountPreferences(accountKey)
    }

    val isNotificationEnabled by lazy {
        preferences.isNotificationEnabled
    }

    fun setIsNotificationEnabled(value: Boolean) = viewModelScope.launch {
        preferences.setIsNotificationEnabled(value)
    }
}
