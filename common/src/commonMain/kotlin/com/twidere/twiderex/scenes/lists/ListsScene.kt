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
package com.twidere.twiderex.scenes.lists

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.ui.LazyUiListsList
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.lists.ListsViewModel
import io.github.seiko.precompose.annotation.NavGraphDestination
import moe.tlaster.precompose.navigation.Navigator
import java.util.Locale


@NavGraphDestination(
  route = Root.Lists.Home,
)
@Composable
fun ListsScene(
  navigator: Navigator,
) {
  TwidereScene {
    InAppNotificationScaffold(
      topBar = {
        AppBar(
          navigationIcon = {
            AppBarNavigationButton()
          },
          title = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_title))
          },
        )
      },
      floatingActionButton = {
        ListsSceneFab(navigator)
      },
      floatingActionButtonPosition = FabPosition.Center
    ) {
      ListsSceneContent(
        navigator,
      )
    }
  }
}

@Composable
fun ListsSceneFab(
  navigator: Navigator,
) {
  val account = LocalActiveAccount.current ?: return
  FloatingActionButton(
    onClick = {
      when (account.type) {
        PlatformType.Twitter -> navigator.navigate(Root.Lists.TwitterCreate)
        PlatformType.StatusNet -> TODO()
        PlatformType.Fanfou -> TODO()
        PlatformType.Mastodon -> navigator.navigate(Root.Lists.MastodonCreateDialog)
      }
    }
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(ListsSceneDefaults.Fab.ContentPadding)
    ) {
      Icon(
        painter = painterResource(res = com.twidere.twiderex.MR.files.ic_add),
        contentDescription = stringResource(
          res = com.twidere.twiderex.MR.strings.scene_lists_icons_create
        ),
        modifier = Modifier.padding(ListsSceneDefaults.Fab.IconPadding)
      )
      Text(
        text = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_modify_create_title)
          .uppercase(Locale.getDefault()),
        style = MaterialTheme.typography.button
      )
    }
  }
}

@Composable
fun ListsSceneContent(
  navigator: Navigator
) {
  val account = LocalActiveAccount.current ?: return
  // if list type is all , display title of each type
  val listsViewMode: ListsViewModel = getViewModel()
  val ownerItems = listsViewMode.ownerSource.collectAsLazyPagingItems()
  val subscribeItems = listsViewMode.subscribedSource.collectAsLazyPagingItems()
  val sourceItems = listsViewMode.source.collectAsLazyPagingItems()
  SwipeToRefreshLayout(
    refreshingState = ownerItems.loadState.refresh is LoadState.Loading,
    onRefresh = { ownerItems.refresh() }
  ) {
    LazyUiListsList(
      listType = account.listType,
      source = sourceItems,
      ownerItems = ownerItems,
      subscribedItems = subscribeItems,
      onItemClicked = {
        navigator.navigate(Root.Lists.Timeline(it.listKey))
      }
    )
  }
}

private object ListsSceneDefaults {
  object Fab {
    val ContentPadding = PaddingValues(horizontal = 22.dp)
    val IconPadding = PaddingValues(end = 17.dp)
  }
}
