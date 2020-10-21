/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.fragment

import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabConstants
import androidx.compose.material.TabConstants.defaultTabIndicatorOffset
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Reply
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedTask
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.viewinterop.viewModel
import androidx.compose.ui.zIndex
import androidx.navigation.fragment.navArgs
import com.twidere.twiderex.R
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.LoadingProgress
import com.twidere.twiderex.component.NetworkImage
import com.twidere.twiderex.component.StatusDivider
import com.twidere.twiderex.component.StatusMediaPreviewItem
import com.twidere.twiderex.component.SwipeToRefreshLayout
import com.twidere.twiderex.component.TimelineStatusComponent
import com.twidere.twiderex.component.TopAppBarElevation
import com.twidere.twiderex.component.UserAvatar
import com.twidere.twiderex.component.itemsGridIndexed
import com.twidere.twiderex.extensions.NavControllerAmbient
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.ui.topBarHeight
import com.twidere.twiderex.viewmodel.user.UserFavouriteTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFragment : JetFragment() {
    private val args by navArgs<UserFragmentArgs>()

    @OptIn(IncomingComposeUpdate::class)
    @Composable
    override fun onCompose() {
        UserComponent(data = args.user) {
            AppBar(
                backgroundColor = MaterialTheme.colors.surface.withElevation(),
                navigationIcon = {
                    AppBarNavigationButton()
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(asset = Icons.Default.Mail)
                    }
                    IconButton(onClick = {}) {
                        Icon(asset = Icons.Default.MoreVert)
                    }
                },
                elevation = 0.dp,
            )
        }
    }
}

@OptIn(ExperimentalLazyDsl::class)
@Composable
@IncomingComposeUpdate
fun UserComponent(
    data: UiUser,
    lazyListState: LazyListState = rememberLazyListState(),
    appBar: @Composable (() -> Unit)? = null,
) {
    val viewModel = viewModel<UserViewModel>()
    val user by viewModel.user.observeAsState(initial = data)

    val tabs = listOf(
        Icons.Default.List,
        Icons.Default.Image,
        Icons.Default.Favorite,
    )
    val (selectedItem, setSelectedItem) = savedInstanceState { 0 }

    val timelineViewModel = viewModel<UserTimelineViewModel>()
    val timeline by timelineViewModel.timeline.observeAsState(initial = emptyList())
    val timelineLoadingMore by timelineViewModel.loadingMore.observeAsState(initial = false)
    val mediaTimeline =
        timeline.filter { it.hasMedia }.flatMap { it.media.map { media -> media to it } }

    val favouriteViewModel = viewModel<UserFavouriteTimelineViewModel>()
    val favourite by favouriteViewModel.timeline.observeAsState(initial = emptyList())
    val favouriteLoadingMore by favouriteViewModel.loadingMore.observeAsState(initial = false)

    val refreshing by viewModel.refreshing.observeAsState(initial = false)

    val coroutineScope = rememberCoroutineScope()

    LaunchedTask(
        selectedItem,
        timeline,
        favourite,
    ) {
        async {
            viewModel.init(user)
        }
        async {
            when {
                selectedItem == 0 && !timeline.any() -> {
                    timelineViewModel.refresh(user)
                }
                selectedItem == 1 && !mediaTimeline.any() -> {
                    timelineViewModel.loadMore(user)
                }
                selectedItem == 2 && !favourite.any() -> {
                    favouriteViewModel.refresh(user)
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(asset = Icons.Default.Reply)
            }
        }
    ) {
        Box {
            val shouldStickyHeaderShown = lazyListState.firstVisibleItemIndex >= 1
            Surface(
                modifier = Modifier.zIndex(1f),
                elevation = if (shouldStickyHeaderShown) TopAppBarElevation else 0.dp
            ) {
                Column {
                    appBar?.invoke()
                    if (shouldStickyHeaderShown) {
                        UserTabsComponent(
                            items = tabs,
                            selectedItem = selectedItem,
                            onItemSelected = {
                                setSelectedItem(it)
                            },
                        )
                    }
                }
            }
            SwipeToRefreshLayout(
                refreshingState = refreshing,
                onRefresh = {
                    timelineViewModel.clear()
                    favouriteViewModel.clear()
                    coroutineScope.launch {
                        viewModel.refresh(user)
                    }
                },
                refreshIndicator = {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .preferredSize(36.dp)
                            .padding(4.dp)
                    )
                },
            ) {
                Column {
                    if (appBar != null) {
                        Spacer(modifier = Modifier.height(topBarHeight)) // Appbar height
                    }
                    // TODO: background color
                    // TODO: header paddings
                    LazyColumn(
                        state = lazyListState
                    ) {
                        item {
                            UserInfo(user)
                        }

                        item {
                            UserTabsComponent(
                                items = tabs,
                                selectedItem = selectedItem,
                                onItemSelected = {
                                    setSelectedItem(it)
                                },
                            )
                        }

                        when (selectedItem) {
                            0 -> {
                                if (timeline.any()) {
                                    itemsIndexed(timeline) { index, item ->
                                        Column {
                                            if (!timelineLoadingMore && index == timeline.lastIndex) {
                                                coroutineScope.launch {
                                                    timelineViewModel.loadMore(user)
                                                }
                                            }
                                            TimelineStatusComponent(item)
                                            if (index != timeline.lastIndex) {
                                                StatusDivider()
                                            }
                                        }
                                    }
                                }
                            }
                            1 -> {
                                if (mediaTimeline.any()) {
                                    itemsGridIndexed(
                                        mediaTimeline,
                                        rowSize = 2,
                                        spacing = standardPadding * 2,
                                        padding = standardPadding * 2,
                                    ) { index, item ->
                                        val navController = NavControllerAmbient.current
                                        if (!timelineLoadingMore && index == mediaTimeline.lastIndex) {
                                            coroutineScope.launch {
                                                timelineViewModel.loadMore(user)
                                            }
                                        }
                                        StatusMediaPreviewItem(
                                            item.first,
                                            modifier = Modifier
                                                .aspectRatio(1F)
                                                .clip(
                                                    RoundedCornerShape(8.dp)
                                                ),
                                            onClick = {
                                                navController.navigate(
                                                    R.id.media_fragment,
                                                    MediaFragmentArgs(
                                                        item.second,
                                                        item.second.media.indexOf(item.first)
                                                    ).toBundle()
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                            2 -> {
                                if (favourite.any()) {
                                    itemsIndexed(favourite) { index, item ->
                                        Column {
                                            if (!timelineLoadingMore && index == favourite.lastIndex) {
                                                coroutineScope.launch {
                                                    favouriteViewModel.loadMore(user)
                                                }
                                            }
                                            TimelineStatusComponent(item)
                                            if (index != favourite.lastIndex) {
                                                StatusDivider()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (timelineLoadingMore || favouriteLoadingMore) {
                            item {
                                val modifier = when (selectedItem) {
                                    0, 1 -> if (timeline.any()) Modifier.fillParentMaxWidth() else Modifier.fillParentMaxSize()
                                    2 -> if (favourite.any()) Modifier.fillParentMaxWidth() else Modifier.fillParentMaxSize()
                                    else -> Modifier.fillParentMaxWidth()
                                }
                                StatusDivider()
                                Box(
                                    modifier = modifier,
                                    alignment = Alignment.TopCenter
                                ) {
                                    LoadingProgress()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserInfo(data: UiUser) {
    val viewModel = viewModel<UserViewModel>()
    val user by viewModel.user.observeAsState(initial = data)
    val loaded by viewModel.loaded.observeAsState(initial = false)
    val relationship by viewModel.relationship.observeAsState()
    val isMe by viewModel.isMe.observeAsState(initial = false)
    val maxBannerSize = 200.dp

    Box(
        modifier = Modifier
            .background(MaterialTheme.colors.surface.withElevation())
    ) {
        // TODO: parallax effect
        user.profileBackgroundImage?.let {
            Box(
                modifier = Modifier
                    .heightIn(max = maxBannerSize)
            ) {
                NetworkImage(
                    url = it,
                )
            }
        }
        Column {
            WithConstraints {
                Spacer(
                    modifier = Modifier.height(
                        min(
                            maxWidth * 160f / 320f - 72.dp / 2,
                            maxBannerSize - 72.dp / 2
                        )
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                alignment = Alignment.Center
            ) {
                Spacer(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .clipToBounds()
                        .background(MaterialTheme.colors.surface.withElevation())
                )
                UserAvatar(
                    user = user,
                    size = 72.dp,
                )
            }
            Spacer(modifier = Modifier.height(standardPadding * 2))
            Row(
                modifier = Modifier
                    .padding(horizontal = standardPadding * 2)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.h6,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "@${user.screenName}",
                    )
                }
                if (isMe) {
                    IconButton(onClick = {}) {
                        Icon(asset = Icons.Default.Edit)
                    }
                } else {
                    relationship?.let {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = if (it.followedBy) "Following" else "Follow",
                                style = MaterialTheme.typography.h6,
                                color = MaterialTheme.colors.primary,
                            )
                            if (it.following) {
                                Text(
                                    text = "Follows you",
                                    style = MaterialTheme.typography.caption,
                                )
                            }
                        }
                    } ?: run {
                        if (!loaded) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(standardPadding * 2))
            ListItem(
                text = {
                    Text(text = user.desc)
                }
            )
            user.website?.let {
                ListItem(
                    icon = {
                        Icon(asset = Icons.Default.Link)
                    },
                    text = {
                        Text(
                            text = it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                )
            }
            user.location?.let {
                ListItem(
                    icon = {
                        Icon(asset = Icons.Default.MyLocation)
                    },
                    text = {
                        Text(
                            text = it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.height(standardPadding * 2))
            Row {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = user.friendsCount.toString())
                    Text(text = "Following")
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = user.followersCount.toString())
                    Text(text = "Followers")
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = user.listedCount.toString())
                    Text(text = "Listed")
                }
            }
            Spacer(modifier = Modifier.height(standardPadding * 2))
        }
    }
}

@Composable
private fun UserTabsComponent(
    items: List<VectorAsset>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
) {
    Surface() {
    }
    TabRow(
        selectedTabIndex = selectedItem,
        backgroundColor = MaterialTheme.colors.surface.withElevation(),
        indicator = { tabPositions ->
            TabConstants.DefaultIndicator(
                modifier = Modifier.defaultTabIndicatorOffset(tabPositions[selectedItem]),
                color = MaterialTheme.colors.primary,
            )
        }
    ) {
        for (i in 0 until items.count()) {
            Tab(
                selected = selectedItem == i,
                onClick = {
                    onItemSelected(i)
                },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = AmbientEmphasisLevels.current.medium.applyEmphasis(
                    AmbientContentColor.current
                ),
            ) {
                Box(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(asset = items[i])
                }
            }
        }
    }
}
