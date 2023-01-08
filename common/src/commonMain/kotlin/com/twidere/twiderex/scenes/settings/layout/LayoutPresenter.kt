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
package com.twidere.twiderex.scenes.settings.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.twidere.twiderex.model.AccountPreferences
import com.twidere.twiderex.model.HomeMenus
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import kotlinx.coroutines.flow.Flow
import kotlin.math.min

private const val MaxMenuCount = 5

@Composable
fun LayoutPresenter(
  event: Flow<LayoutEvent>,
): LayoutState {
  val accountState = CurrentAccountPresenter()
  if (accountState !is CurrentAccountState.Account) {
    return LayoutState.NoAccount
  }
  val user = remember(accountState.account) {
    accountState.account.toUi()
  }
  val menuOrder by accountState.account.preferences.homeMenuOrder.collectAsState(
    HomeMenus.values().map { it to it.showDefault }
  )
  val menus = remember(menuOrder) {
    menuOrder.filter { it.first.supportedPlatformType.contains(accountState.account.type) }
      .groupBy {
        it.second
      }.map {
        listOf(
          it.key
        ) + it.value.map { it.first }
      }.flatten().let {
        if (it.firstOrNull() != true) {
          listOf(true) + it
        } else {
          it
        }
      }.let {
        if (!it.contains(false)) {
          it + false
        } else {
          it
        }
      }
  }

  LaunchedEffect(Unit) {
    event.collect { event ->
      when (event) {
        is LayoutEvent.UpdateMenuOrder -> {
          val (oldIndex, newIndex) = event
          updateMenu(
            oldIndex = oldIndex,
            newIndex = newIndex,
            menus = menus,
            accountPreferences = accountState.account.preferences,
          )
        }
        is LayoutEvent.AddMenu -> {
          val newIndex = menus.indexOf(false).let {
            if (it == MaxMenuCount + 1) {
              it - 1
            } else {
              it
            }
          }
          updateMenu(
            oldIndex = event.index,
            newIndex = newIndex,
            menus = menus,
            accountPreferences = accountState.account.preferences,
          )
        }
        is LayoutEvent.RemoveMenu -> {
          val newIndex = menus.indexOf(false)
          updateMenu(
            oldIndex = event.index,
            newIndex = newIndex,
            menus = menus,
            accountPreferences = accountState.account.preferences,
          )
        }
      }
    }
  }

  return LayoutState.Data(
    user = user,
    menus = menus
  )
}

private suspend fun updateMenu(
  oldIndex: Int,
  newIndex: Int,
  menus: List<Any>,
  accountPreferences: AccountPreferences,
) {
  menus.toMutableList().let { list ->
    list.add(newIndex, list.removeAt(oldIndex))
    list.remove(true)
    min(list.indexOf(false), MaxMenuCount).let { index ->
      list.subList(0, index).filterIsInstance<HomeMenus>()
        .map { it to true } + list.subList(index, list.size)
        .filterIsInstance<HomeMenus>().map { it to false }
    }.let {
      accountPreferences.setHomeMenuOrder(it)
    }
  }
}

interface LayoutState {
  object NoAccount : LayoutState
  data class Data(
    val user: UiUser,
    val menus: List<Any>
  ) : LayoutState
}

interface LayoutEvent {
  data class UpdateMenuOrder(val oldIndex: Int, val newIndex: Int) : LayoutEvent
  data class AddMenu(val index: Int) : LayoutEvent
  data class RemoveMenu(val index: Int) : LayoutEvent
}
