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
package com.twidere.twiderex.viewmodel.settings

import com.twidere.twiderex.model.HomeMenus
import com.twidere.twiderex.repository.AccountRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import kotlin.math.min

private const val MaxMenuCount = 5

class LayoutViewModel(
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.mapNotNull { it }
    }

    fun updateHomeMenu(oldIndex: Int, newIndex: Int, menus: List<Any>) = viewModelScope.launch {
        menus.toMutableList().let { list ->
            list.add(newIndex, list.removeAt(oldIndex))
            list.remove(true)
            list.indexOf(false).let { min(it, MaxMenuCount) }.let { index ->
                list.subList(0, index).filterIsInstance<HomeMenus>()
                    .map { it to true } + list.subList(index, list.size)
                    .filterIsInstance<HomeMenus>().map { it to false }
            }.let {
                account.firstOrNull()?.preferences?.setHomeMenuOrder(it)
            }
        }
    }

    fun removeMenu(current: Int, menus: List<Any>) {
        val newIndex = menus.indexOf(false)
        updateHomeMenu(
            oldIndex = current,
            newIndex = newIndex,
            menus = menus,
        )
    }

    fun addMenu(current: Int, menus: List<Any>) {
        val newIndex = menus.indexOf(false).let {
            if (it == MaxMenuCount + 1) {
                it - 1
            } else {
                it
            }
        }
        updateHomeMenu(
            oldIndex = current,
            newIndex = newIndex,
            menus = menus,
        )
    }

    val user by lazy {
        account.map {
            it.toUi()
        }
    }
}
