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
package com.twidere.twiderex.component

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.ui.LazyUiUserList
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.navigation.UserNavigationData

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserListComponent(
  source: LazyPagingItems<UiUser>,
  action: @Composable (user: UiUser) -> Unit = {},
  userNavigationData: UserNavigationData,
) {
  SwipeToRefreshLayout(
    refreshingState = source.loadState.refresh is LoadState.Loading,
    onRefresh = {
      source.refreshOrRetry()
    }
  ) {
    LazyUiUserList(
      items = source,
      userNavigationData = userNavigationData,
      onItemClicked = {
        userNavigationData.statusNavigation.toUser(it)
      },
      action = action,
    )
  }
}
