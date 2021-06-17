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
package com.twidere.twiderex.scenes.search

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarDefaults
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.scenes.search.tabs.MastodonSearchHashtagItem
import com.twidere.twiderex.scenes.search.tabs.SearchTweetsItem
import com.twidere.twiderex.scenes.search.tabs.SearchUserItem
import com.twidere.twiderex.scenes.search.tabs.TwitterSearchMediaItem
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.search.SearchSaveViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun SearchScene(keyword: String) {
    val account = LocalActiveAccount.current ?: return
    val navigator = LocalNavigator.current

    val viewModel =
        assistedViewModel<SearchSaveViewModel.AssistedFactory, SearchSaveViewModel>(
            account, keyword
        ) {
            it.create(account, keyword)
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
    val isSaved by viewModel.isSaved.observeAsState(false)
    val loading by viewModel.loading.observeAsState(initial = false)
    TwidereScene {
        InAppNotificationScaffold {
            Column {
                Surface(
                    elevation = AppBarDefaults.TopAppBarElevation,
                ) {
                    Column {
                        AppBar(
                            navigationIcon = {
                                AppBarNavigationButton()
                            },
                            elevation = 0.dp,
                            title = {
                                ProvideTextStyle(value = MaterialTheme.typography.body1) {
                                    Row {
                                        Text(
                                            modifier = Modifier
                                                .clickable(
                                                    onClick = {
                                                        navigator.searchInput(keyword)
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
                                        if (loading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(SearchSceneDefaults.Loading.size)
                                                    .padding(SearchSceneDefaults.Loading.padding),
                                                strokeWidth = SearchSceneDefaults.Loading.width,
                                                color = SearchSceneDefaults.Loading.color
                                            )
                                        } else if (!isSaved) {
                                            IconButton(
                                                onClick = {
                                                    if (!loading && !isSaved) viewModel.save()
                                                }
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_device_floppy),
                                                    contentDescription = stringResource(
                                                        id = R.string.accessibility_scene_search_save
                                                    )
                                                )
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
                                    modifier = Modifier.pagerTabIndicatorOffset(
                                        pagerState,
                                        tabPositions
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
                                            pagerState.animateScrollToPage(index)
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
                    HorizontalPager(state = pagerState) { page ->
                        tabs[page].Content(keyword = keyword)
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
        val color = Color(0x21212121)
    }
}
