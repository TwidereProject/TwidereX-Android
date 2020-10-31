package com.twidere.twiderex.scenes

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedTask
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.twidere.twiderex.extensions.navViewModel
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.ExpandedStatusComponent
import com.twidere.twiderex.component.LoadingProgress
import com.twidere.twiderex.component.StatusDivider
import com.twidere.twiderex.component.StatusLineComponent
import com.twidere.twiderex.component.TimelineStatusComponent
import com.twidere.twiderex.component.loading
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.twitter.TwitterStatusViewModel
import kotlinx.coroutines.launch

@Composable
fun StatusScene(statusId: String) {
    //TODO: load status
}


@OptIn(ExperimentalLazyDsl::class)
@Composable
fun StatusScene(data: UiStatus) {
    val viewModel = navViewModel<TwitterStatusViewModel>()
    val loadingPrevious by viewModel.loadingPrevious.observeAsState(initial = false)
    val loadingMore by viewModel.loadingMore.observeAsState(initial = false)
    val status by viewModel.status.observeAsState(initial = data)
    val moreConversations by viewModel.moreConversations.observeAsState(initial = emptyList())
    val previousConversations by viewModel.previousConversations.observeAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    LaunchedTask {
        viewModel.init(status)
    }
    Scaffold(
        topBar = {
            AppBar(
                title = {
                    Text(text = "Tweet")
                },
                navigationIcon = {
                    AppBarNavigationButton()
                }
            )
        }
    ) {
        if (loadingPrevious) {
            Column {
                ExpandedStatusComponent(
                    status = status,
                )
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
                            ExpandedStatusComponent(
                                status = status,
                            )
                        }
                        Divider(
                            modifier = Modifier.padding(horizontal = standardPadding * 2)
                        )
                    }
                }
                if (moreConversations.any()) {
                    itemsIndexed(moreConversations) { index, item ->
                        val modifier = if (!loadingMore && index == moreConversations.lastIndex) {
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