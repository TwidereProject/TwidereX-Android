package com.twidere.twiderex.component

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.twitter.timeline.TimelineViewModel
import kotlinx.coroutines.launch

@OptIn(IncomingComposeUpdate::class)
@Composable
fun TimelineComponent(viewModel: TimelineViewModel) {
    val items by viewModel.source.observeAsState(initial = listOf())
    val loadingBetween by viewModel.loadingBetween.observeAsState(initial = listOf())
    val loadingMore by viewModel.loadingMore.observeAsState(initial = false)
    val refreshing by viewModel.refreshing.observeAsState(initial = false)
    val scope = rememberCoroutineScope()
    SwipeToRefreshLayout(
        refreshingState = refreshing,
        onRefresh = {
            scope.launch {
                viewModel.refresh()
            }
        },
        refreshIndicator = {
            Surface(elevation = 10.dp, shape = CircleShape) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .preferredSize(36.dp)
                        .padding(4.dp)
                )
            }
        },
    ) {
        if (items.any()) {
            val listState =
                rememberLazyListState(initialFirstVisibleItemIndex = viewModel.restoreScrollState())
            onDispose {
                viewModel.saveScrollState(listState.firstVisibleItemIndex)
            }
            LazyColumnForIndexed(
                items = items,
                state = listState,
            ) { index, item ->
                Column {
                    if (!loadingMore && index == items.size - 1) {
                        scope.launch {
                            viewModel.loadMore()
                        }
                    }
                    TimelineStatusComponent(
                        item,
                    )
                    if (index != items.size - 1) {
                        when {
                            loadingBetween.contains(item.statusId) -> {
                                LoadingProgress()
                            }
                            item.isGap -> {
                                Divider()
                                TextButton(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    onClick = {
                                        scope.launch {
                                            viewModel.loadBetween(
                                                item.statusId,
                                                items[index + 1].statusId,
                                            )
                                        }
                                    },
                                ) {
                                    Text("Load more")
                                }
                                Divider()
                            }
                            else -> {
                                Divider(
                                    modifier = Modifier.padding(
                                        start = profileImageSize + standardPadding,
                                        end = standardPadding
                                    )
                                )
                            }
                        }
                    }
                    if (loadingMore && index == items.size - 1) {
                        LoadingProgress()
                    }
                }
            }
        }
    }
}
