/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.scenes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
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
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.foundation.loading
import com.twidere.twiderex.component.status.ExpandedStatusComponent
import com.twidere.twiderex.component.status.StatusDivider
import com.twidere.twiderex.component.status.StatusLineComponent
import com.twidere.twiderex.component.status.TimelineStatusComponent
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.twitter.TwitterStatusViewModel
import kotlinx.coroutines.launch
@Composable
fun StatusScene(statusId: String) {
    val account = AmbientActiveAccount.current ?: return
    val viewModel = assistedViewModel<TwitterStatusViewModel.AssistedFactory, TwitterStatusViewModel> {
        it.create(account, statusId)
    }
    val loadingPrevious by viewModel.loadingPrevious.observeAsState(initial = false)
    val loadingMore by viewModel.loadingMore.observeAsState(initial = false)
    val status by viewModel.status.observeAsState()
    val moreConversations by viewModel.moreConversations.observeAsState(initial = emptyList())
    val previousConversations by viewModel.previousConversations.observeAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    TwidereXTheme {
        Scaffold(
            topBar = {
                AppBar(
                    title = {
                        Text(text = stringResource(id = R.string.title_tweet))
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
                            status = it,
                        )
                    }
                    Divider(
                        modifier = Modifier.padding(horizontal = standardPadding * 2)
                    )
                    LoadingProgress()
                }
            } else {
                LazyColumn(
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
                        Column(
                            modifier = if (moreConversations.any()) {
                                Modifier
                            } else {
                                Modifier.fillParentMaxHeight()
                            }
                        ) {
                            StatusLineComponent(
                                lineUp = previousConversations.any(),
                            ) {
                                status?.let {
                                    ExpandedStatusComponent(
                                        status = it,
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
                            val modifier =
                                if (!loadingMore && index == moreConversations.lastIndex) {
                                    Modifier.fillParentMaxHeight()
                                } else {
                                    Modifier
                                }
                            Box(
                                modifier = modifier
                            ) {
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
