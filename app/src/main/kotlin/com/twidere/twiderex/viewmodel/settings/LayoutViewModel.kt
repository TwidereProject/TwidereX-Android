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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.scenes.home.HomeMenus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class LayoutViewModel @AssistedInject constructor(
    @Assisted private val account: AccountDetails,
) : ViewModel() {
    fun updateHomeMenu(oldIndex: Int, newIndex: Int, menus: List<Any>) = viewModelScope.launch {
        menus.toMutableList().let { list ->
            list.add(newIndex, list.removeAt(oldIndex))
            list.indexOf(false).let { index ->
                list.subList(0, index).filterIsInstance<HomeMenus>()
                    .map { it to true } + list.subList(index, list.size)
                    .filterIsInstance<HomeMenus>().map { it to false }
            }.let {
                account.preferences.setHomeMenuOrder(it)
            }
        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            account: AccountDetails,
        ): LayoutViewModel
    }

    val user by lazy {
        account.toUi()
    }
}
