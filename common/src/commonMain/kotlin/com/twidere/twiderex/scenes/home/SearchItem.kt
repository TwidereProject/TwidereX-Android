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
package com.twidere.twiderex.scenes.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.paging.compose.items
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.navigation.hashtag
import com.twidere.twiderex.component.navigation.search
import com.twidere.twiderex.component.navigation.searchInput
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.component.trend.MastodonTrendItem
import com.twidere.twiderex.component.trend.TwitterTrendItem
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.model.HomeNavigationItem
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.scenes.home.presenter.SearchItemState
import com.twidere.twiderex.scenes.home.presenter.TrendingPresenter
import com.twidere.twiderex.scenes.search.presenter.SearchInputEvent
import com.twidere.twiderex.scenes.search.presenter.SearchInputState
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import moe.tlaster.precompose.navigation.Navigator

class SearchItem : HomeNavigationItem() {

  @Composable
  override fun name(): String = stringResource(com.twidere.twiderex.MR.strings.scene_search_title)
  override val route: String
    get() = Root.Search.Home

  @Composable
  override fun icon(): Painter = painterResource(res = com.twidere.twiderex.MR.files.ic_search)

  override val withAppBar: Boolean
    get() = false

  @Composable
  override fun Content(navigator: Navigator) {
    SearchSceneContent(navigator)
  }
}

@Composable
fun SearchScene(
  navigator: Navigator,
) {
  TwidereScene {
    InAppNotificationScaffold(
      topBar = {
        AppBar(
          title = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_search_title))
          },
          navigationIcon = {
            AppBarNavigationButton(
              onBack = {
                navigator.popBackStack()
              }
            )
          }
        )
      }
    ) {
      SearchSceneContent(navigator)
    }
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchSceneContent(
  navigator: Navigator
) {
  val account = LocalActiveAccount.current ?: return
  val (state, channel) = rememberPresenterState(useImmediateClock = true) { TrendingPresenter(it) }
  if (state !is SearchItemState.Data) {
    return
  }
  Scaffold(
    topBar = {
      AppBar(
        title = {
          ProvideTextStyle(value = MaterialTheme.typography.body1) {
            Row(
              modifier = Modifier.clickable(
                onClick = {
                  navigator.searchInput()
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
              )
            ) {
              CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.medium
              ) {
                Text(
                  modifier = Modifier
                    .weight(1F)
                    .align(Alignment.CenterVertically),
                  text = stringResource(res = com.twidere.twiderex.MR.strings.scene_search_search_bar_placeholder),
                )
              }
              IconButton(
                onClick = {
                  navigator.searchInput()
                }
              ) {
                Icon(
                  painter = painterResource(res = com.twidere.twiderex.MR.files.ic_search),
                  contentDescription = stringResource(
                    res = com.twidere.twiderex.MR.strings.scene_search_title
                  ),
                  modifier = Modifier.size(24.dp),
                )
              }
            }
          }
        }
      )
    }
  ) {
    LazyColumn {
      if (state.searchInputState is SearchInputState.Data) {
        item {
          if (state.searchInputState.savedSource.isNotEmpty()) ListItem {
            Text(
              text = stringResource(res = com.twidere.twiderex.MR.strings.scene_search_saved_search),
              style = MaterialTheme.typography.button
            )
          }
        }
        items(
          items = state.searchInputState.savedSource
        ) {
          ListItem(
            modifier = Modifier.clickable(
              onClick = {
                channel.trySend(SearchInputEvent.AddOrUpgradeEvent(it.content))
                navigator.search(it.content)
              }

            ),
            trailing = {
              IconButton(
                onClick = {
                  channel.trySend(SearchInputEvent.RemoveEvent(it))
                }
              ) {
                Icon(
                  painter = painterResource(res = com.twidere.twiderex.MR.files.ic_trash_can),
                  contentDescription = stringResource(
                    res = com.twidere.twiderex.MR.strings.common_controls_actions_remove
                  ),
                  modifier = Modifier.size(24.dp),
                )
              }
            },
            text = {
              Text(
                text = it.content,
                style = MaterialTheme.typography.subtitle1
              )
            },
          )
        }
        item {
          if (state.searchInputState.showExpand) ListItem(
            modifier = Modifier.clickable {
              channel.trySend(SearchInputEvent.ChangeExpand(!state.searchInputState.expandSearch))
            }
          ) {
            Text(
              text = if (state.searchInputState.expandSearch) {
                stringResource(res = com.twidere.twiderex.MR.strings.scene_search_show_less)
              } else {
                stringResource(res = com.twidere.twiderex.MR.strings.scene_search_show_more)
              },
              style = MaterialTheme.typography.subtitle1,
              color = MaterialTheme.colors.primary
            )
          }
        }
      }

      if (state.data.itemCount > 0) {
        item {
          Column {
            Divider()
            ListItem {
              when (account.type) {
                PlatformType.Twitter ->
                  Text(
                    text = stringResource(res = com.twidere.twiderex.MR.strings.scene_trends_world_wide),
                    style = MaterialTheme.typography.button
                  )
                PlatformType.StatusNet -> TODO()
                PlatformType.Fanfou -> TODO()
                PlatformType.Mastodon ->
                  Text(
                    text = stringResource(res = com.twidere.twiderex.MR.strings.scene_trends_world_wide),
                    style = MaterialTheme.typography.button
                  )
              }
            }
          }
        }
      }
      items(state.data) {
        it?.let { trend ->
          when (account.type) {
            PlatformType.Twitter -> TwitterTrendItem(
              trend = it,
              onClick = {
                channel.trySend(SearchInputEvent.AddOrUpgradeEvent(trend.query))
                navigator.search(trend.query)
              }
            )
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon -> MastodonTrendItem(
              trend = it,
              onClick = {
                navigator.hashtag(it.query)
              }
            )
          }
        }
      }
    }
  }
}
