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
package com.twidere.twiderex.viewmodel

import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.repository.AccountRepository
import moe.tlaster.precompose.viewmodel.ViewModel

class ActiveAccountViewModel(
    private val repository: AccountRepository,
) : ViewModel() {
    fun setActiveAccount(it: AccountDetails) {
        repository.setCurrentAccount(it)
    }

    fun deleteAccount(detail: AccountDetails) {
        repository.delete(detail)
    }

    fun getTargetPlatformDefault(type: PlatformType): AccountDetails? {
        return repository.getFirstByType(type)
    }

    val account by lazy {
        repository.activeAccount
    }
    val allAccounts by lazy {
        repository.accounts
    }
}
