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
package com.twidere.twiderex.scenes.twitter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.foundation.loading
import com.twidere.twiderex.component.lazy.LazyColumn2
import com.twidere.twiderex.component.status.ExpandedStatusComponent
import com.twidere.twiderex.component.status.StatusDivider
import com.twidere.twiderex.component.status.StatusLineComponent
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.twitter.TwitterStatusViewModel
import kotlinx.coroutines.launch

@Composable
fun TwitterStatusScene(statusKey: MicroBlogKey) {
    val account = LocalActiveAccount.current ?: return
    val viewModel =
        assistedViewModel<TwitterStatusViewModel.AssistedFactory, TwitterStatusViewModel> {
            it.create(account, statusKey)
        }
    val loadingPrevious by viewModel.loadingPrevious.observeAsState(initial = false)
    val loadingMore by viewModel.loadingMore.observeAsState(initial = false)
    val status by viewModel.status.observeAsState()
    val moreConversations by viewModel.moreConversations.observeAsState(initial = emptyList())
    val previousConversations by viewModel.previousConversations.observeAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    title = {
                        Text(text = stringResource(id = R.string.scene_search_tabs_tweets))
                    },
                    navigationIcon = {
                        AppBarNavigationButton()
                    }
                )
            }
        ) {
            if (loadingPrevious) {
                Column {
                    status?.let {
                        ExpandedStatusComponent(
                            data = it,
                        )
                    }
                    Divider(
                        modifier = Modifier.padding(horizontal = standardPadding * 2)
                    )
                    LoadingProgress()
                }
            } else {
                LazyColumn2(
                    state = rememberLazyListState(initialFirstVisibleItemIndex = previousConversations.size)
                ) {
                    itemsIndexed(previousConversations) { index, item ->
                        StatusLineComponent(
                            lineUp = index != 0,
                            lineDown = true,
                        ) {
                            TimelineStatusComponent(data = item)
                        }
                        StatusDivider()
                    }
                    item {
                        Column {
                            StatusLineComponent(
                                lineUp = previousConversations.any(),
                            ) {
                                status?.let {
                                    ExpandedStatusComponent(
                                        data = it,
                                    )
                                }
                            }
                            Divider(
                                modifier = Modifier.padding(horizontal = standardPadding * 2)
                            )
                        }
                    }
                    if (moreConversations.any()) {
                        itemsIndexed(moreConversations) { index, item ->
                            Box {
                                TimelineStatusComponent(data = item)
                            }
                            if (index != moreConversations.lastIndex || loadingMore) {
                                StatusDivider()
                            }
                            if (index == moreConversations.lastIndex && !loadingMore) {
                                scope.launch {
                                    viewModel.loadMore()
                                }
                            }
                        }
                    }
                    if (loadingMore) {
                        loading()
                    }
                }
            }
        }
    }
}
