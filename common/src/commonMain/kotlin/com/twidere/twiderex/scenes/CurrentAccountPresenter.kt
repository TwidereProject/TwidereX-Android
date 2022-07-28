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
package com.twidere.twiderex.scenes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.repository.AccountRepository
import kotlinx.coroutines.flow.map

@Composable
fun CurrentAccountPresenter(
  accountRepository: AccountRepository = get(),
): CurrentAccountState {
  val state by accountRepository.activeAccount.map {
    if (it == null) {
      CurrentAccountState.Empty
    } else {
      CurrentAccountState.Account(it)
    }
  }.collectAsState(CurrentAccountState.Empty)
  return state
}

sealed interface CurrentAccountState {
  data class Account(val account: AccountDetails) : CurrentAccountState
  object Empty : CurrentAccountState
}
