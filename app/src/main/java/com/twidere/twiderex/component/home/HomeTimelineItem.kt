package com.twidere.twiderex.component.home

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ColumnScope.align
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.twidere.twiderex.component.SwipeToRefreshLayout
import com.twidere.twiderex.component.TimelineStatusComponent
import com.twidere.twiderex.component.profileImageSize
import com.twidere.twiderex.component.standardPadding
import com.twidere.twiderex.viewmodel.twitter.HomeTimelineViewModel
import kotlinx.coroutines.launch

class HomeTimelineItem : HomeNavigationItem() {
    override val name: String
        get() = "Home"
    override val icon: VectorAsset
        get() = Icons.Default.Home

    @Composable
    override fun onCompose() {
        val viewModel = viewModel<HomeTimelineViewModel>()
        val items by viewModel.source.observeAsState(listOf())
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
                                            items[index + 1].status.status.statusId
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
