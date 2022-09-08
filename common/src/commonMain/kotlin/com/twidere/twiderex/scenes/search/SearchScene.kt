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
package com.twidere.twiderex.scenes.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarDefaults
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.Pager
import com.twidere.twiderex.component.foundation.rememberPagerState
import moe.tlaster.precompose.navigation.Navigator
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.extensions.rememberPresenterState
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.scenes.search.presenter.SearchSaveEvent
import com.twidere.twiderex.scenes.search.presenter.SearchSavePresenter
import com.twidere.twiderex.scenes.search.presenter.SearchSaveState
import com.twidere.twiderex.scenes.search.tabs.MastodonSearchHashtagItem
import com.twidere.twiderex.scenes.search.tabs.SearchTweetsItem
import com.twidere.twiderex.scenes.search.tabs.SearchUserItem
import com.twidere.twiderex.scenes.search.tabs.TwitterSearchMediaItem
import com.twidere.twiderex.twitterHost1
import com.twidere.twiderex.twitterHost2
import com.twidere.twiderex.twitterHost3
import com.twidere.twiderex.twitterHost4
import com.twidere.twiderex.twitterHost5
import com.twidere.twiderex.twitterHost6
import com.twidere.twiderex.twitterSearchDeeplinkSuffix
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import io.github.seiko.precompose.annotation.NavGraphDestination
import io.github.seiko.precompose.annotation.Path
import kotlinx.coroutines.launch


@NavGraphDestination(
  route = Root.Search.Result.route,
  deepLink = [
    "$twitterHost1$twitterSearchDeeplinkSuffix",
    "$twitterHost2$twitterSearchDeeplinkSuffix",
    "$twitterHost3$twitterSearchDeeplinkSuffix",
    "$twitterHost4$twitterSearchDeeplinkSuffix",
    "$twitterHost5$twitterSearchDeeplinkSuffix",
    "$twitterHost6$twitterSearchDeeplinkSuffix",
  ]
)
@Composable
fun SearchScene(
  @Path("keyword") keyword: String,
  navigator: Navigator,
) {
  val account = LocalActiveAccount.current ?: return

  val (state, channel) = rememberPresenterState<SearchSaveState, SearchSaveEvent> {
    SearchSavePresenter(it, content = keyword)
  }

  val tabs = remember {
    when (account.type) {
      PlatformType.Twitter -> listOf(
        SearchTweetsItem(),
        TwitterSearchMediaItem(),
        SearchUserItem()
      )
      else -> listOf(
        SearchTweetsItem(),
        SearchUserItem(),
        MastodonSearchHashtagItem(),
      )
    }
  }
  val pagerState = rememberPagerState(pageCount = tabs.size)
  val scope = rememberCoroutineScope()
  TwidereScene {
    InAppNotificationScaffold {
      Column {
        Surface(
          elevation = AppBarDefaults.TopAppBarElevation,
        ) {
          Column {
            AppBar(
              navigationIcon = {
                AppBarNavigationButton(
                  popBackStack = {
                    navigator.popBackStack()
                  }
                )
              },
              elevation = 0.dp,
              title = {
                ProvideTextStyle(value = MaterialTheme.typography.body1) {
                  Row {
                    Text(
                      modifier = Modifier
                        .clickable(
                          onClick = {
                            navigator.goBack()
                          },
                          indication = null,
                          interactionSource = remember { MutableInteractionSource() }
                        )
                        .align(Alignment.CenterVertically)
                        .weight(1F),
                      text = keyword,
                      maxLines = 1,
                      textAlign = TextAlign.Start,
                    )
                    if (state is SearchSaveState.Data) {
                      if (state.loading) {
                        CircularProgressIndicator(
                          modifier = Modifier
                            .size(SearchSceneDefaults.Loading.size)
                            .padding(SearchSceneDefaults.Loading.padding),
                          strokeWidth = SearchSceneDefaults.Loading.width,
                          color = MaterialTheme.colors.onSurface.copy(0.08f)
                        )
                      } else if (!state.isSaved) {
                        IconButton(
                          onClick = {
                            channel.trySend(SearchSaveEvent.Save)
                          }
                        ) {
                          Icon(
                            painter = painterResource(res = com.twidere.twiderex.MR.files.ic_device_floppy),
                            contentDescription = stringResource(
                              res = com.twidere.twiderex.MR.strings.accessibility_scene_search_save
                            )
                          )
                        }
                      }
                    }
                  }
                }
              }
            )

            TabRow(
              selectedTabIndex = pagerState.currentPage,
              backgroundColor = MaterialTheme.colors.surface.withElevation(),
              indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                  modifier = Modifier.tabIndicatorOffset(
                    tabPositions[pagerState.currentPage]
                  ),
                  color = MaterialTheme.colors.primary,
                )
              }
            ) {
              tabs.forEachIndexed { index, item ->
                Tab(
                  selected = pagerState.currentPage == index,
                  onClick = {
                    scope.launch {
                      pagerState.currentPage = index
                      // pagerState.animateScrollToPage(index)
                    }
                  },
                  content = {
                    Box(
                      modifier = Modifier.padding(16.dp)
                    ) {
                      Text(text = item.name())
                    }
                  },
                )
              }
            }
          }
        }
        Box(
          modifier = Modifier.weight(1F),
        ) {
          Pager(state = pagerState) {
            tabs[page].Content(keyword = keyword, navigator = navigator)
          }
        }
      }
    }
  }
}

private object SearchSceneDefaults {
  object Loading {
    val padding = PaddingValues(12.dp)
    val size = 48.dp
    val width = 2.dp
  }
}
