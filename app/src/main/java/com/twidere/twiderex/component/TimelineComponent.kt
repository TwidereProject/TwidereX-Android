package com.twidere.twiderex.component

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ColumnScope.align
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.twitter.timeline.TimelineViewModel
import kotlinx.coroutines.launch

@Composable
fun TimelineComponent(viewModel: TimelineViewModel) {
    val items by viewModel.source.observeAsState(initial = listOf())
    val loadingBetween by viewModel.loadingBetween.observeAsState(initial = listOf())
    val loadingMore by viewModel.loadingMore.observeAsState(initial = false)
    var refreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    SwipeToRefreshLayout(
        refreshingState = refreshing,
        onRefresh = {
            scope.launch {
                refreshing = true
                viewModel.refresh()
                refreshing = false
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
        LazyColumnForIndexed(
            items = items,
        ) { index, item ->
            Column {
                if (!loadingMore && index == items.size - 1) {
                    scope.launch {
                        viewModel.loadMore()
                    }
                }
                TimelineStatusComponent(item)
                if (index != items.size - 1) {
                    Divider(
                        modifier = Modifier.padding(
                            start = profileImageSize + standardPadding,
                            end = standardPadding
                        )
                    )
                }
                if (index != items.size - 1 && item.timeline.isGap) {
                    if (loadingBetween.contains(item.status.status.statusId)) {
                        LoadingProgress()
                    } else {
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                scope.launch {
                                    viewModel.loadBetween(
                                        item.status.status.statusId,
                                        items[index + 1].status.status.statusId,
                                        item,
                                    )
                                }
                            },
                        ) {
                            Text("Load more")
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

@Composable
private fun LoadingProgress() {
    CircularProgressIndicator(
        modifier = Modifier
            .defaultMinSizeConstraints(
                minHeight = ButtonConstants.DefaultMinHeight,
            )
            .padding(ButtonConstants.DefaultContentPadding)
            .align(Alignment.CenterHorizontally),
    )
}
