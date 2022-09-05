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
package com.twidere.twiderex.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import moe.tlaster.precompose.navigation.Navigator
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.model.ui.UiUser

@Immutable
data class StatusNavigationData(
  val toUser: (UiUser) -> Unit = { },
  val toStatus: (UiStatus) -> Unit = { },
  val toMedia: (MicroBlogKey) -> Unit = { },
  val toMediaWithIndex: (MicroBlogKey, Int) -> Unit = { _: MicroBlogKey, _: Int -> },
  val openLink: (String) -> Unit = { },
  val composeNavigationData: ComposeNavigationData = ComposeNavigationData(),
)

@Composable
fun rememberStatusNavigationData(
  navigator: Navigator
): StatusNavigationData {
  val composeNavigationData = rememberComposeNavigationData(navigator)
  val statusNavigation by remember(navigator) {
    mutableStateOf(
      StatusNavigationData(
        composeNavigationData = composeNavigationData,
        toUser = {
          // navigator.user(it)
        },
        toStatus = {
          // navigator.status(it)
        },
        toMedia = {
          // navigator.media(it)
        },
        toMediaWithIndex = { key, index ->
          // navigator.media(key, index)
        },
        openLink = {
          // navigator.openLink(it)
        }
      )
    )
  }
  return statusNavigation
}

data class UserNavigationData(
  val statusNavigation: StatusNavigationData = StatusNavigationData(),
)

@Composable
fun rememberUserNavigationData(navigator: Navigator): UserNavigationData {
  val statusNavigation = rememberStatusNavigationData(navigator)
  val userNavigation = remember {
    UserNavigationData(statusNavigation = statusNavigation)
  }
  return userNavigation
}

@Immutable
data class ComposeNavigationData(
  val compose: (ComposeType, MicroBlogKey) -> Unit = { _, _ -> },
)
@Composable
fun rememberComposeNavigationData(navigator: Navigator): ComposeNavigationData {
  val composeNavigationData = remember {
    ComposeNavigationData(
      compose = { type, key ->
        // navigator.compose(type, key)
      }
    )
  }
  return composeNavigationData
}

@Immutable
data class DMNavigationData(
  val statusNavigation: StatusNavigationData = StatusNavigationData(),
)

@Composable
fun rememberDMNavigationData(
  navigator: Navigator,
): DMNavigationData {
  val statusNavigation = rememberStatusNavigationData(navigator)
  val dmNavigationData = remember {
    DMNavigationData(statusNavigation = statusNavigation)
  }
  return dmNavigationData
}
