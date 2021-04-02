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
package com.twidere.twiderex.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.google.accompanist.insets.statusBarsHeight
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.IconTabsComponent
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.Pager
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.foundation.TabScaffold
import com.twidere.twiderex.component.foundation.rememberPagerState
import com.twidere.twiderex.component.lazy.LazyColumn2
import com.twidere.twiderex.component.lazy.collectAsLazyPagingItems
import com.twidere.twiderex.component.lazy.itemsPagingGridIndexed
import com.twidere.twiderex.component.lazy.ui.LazyUiStatusList
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.status.HtmlText
import com.twidere.twiderex.component.status.ResolvedLink
import com.twidere.twiderex.component.status.StatusMediaPreviewItem
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.component.status.withAvatarClip
import com.twidere.twiderex.db.model.TwitterUrlEntity
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.navigation.twidereXSchema
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.LocalVideoPlayback
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.ui.statusBarColor
import com.twidere.twiderex.viewmodel.user.UserFavouriteTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserMediaTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserViewModel
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.NavController

@Composable
fun UserComponent(
    userKey: MicroBlogKey,
    extendViewIntoStatusBar: Boolean = false,
) {
    val account = LocalActiveAccount.current ?: return
    val viewModel = assistedViewModel<UserViewModel.AssistedFactory, UserViewModel>(
        account,
        userKey,
    ) {
        it.create(account, userKey)
    }
    val tabs = listOf(
        UserTabComponent(
            painterResource(id = R.drawable.ic_float_left),
            stringResource(id = R.string.accessibility_scene_user_tab_status)
        ) {
            UserStatusTimeline(userKey = userKey, viewModel = viewModel)
        },
        UserTabComponent(
            painterResource(id = R.drawable.ic_photo),
            stringResource(id = R.string.accessibility_scene_user_tab_media)
        ) {
            UserMediaTimeline(userKey = userKey)
        },
    ).let {
        if (viewModel.isMe || userKey.host == MicroBlogKey.TwitterHost) {
            it + UserTabComponent(
                painterResource(id = R.drawable.ic_heart),
                stringResource(id = R.string.accessibility_scene_user_tab_favourite)
            ) {
                UserFavouriteTimeline(userKey = userKey)
            }
        } else {
            it
        }
    }
    val refreshing by viewModel.refreshing.observeAsState(initial = false)
    SwipeToRefreshLayout(
        refreshingState = refreshing,
        onRefresh = {
            viewModel.refresh()
        },
    ) {
        var alpha by remember {
            mutableStateOf(0f)
        }
        TabScaffold(
            onScroll = {
                alpha = it
            },
            appbar = {
                if (extendViewIntoStatusBar) {
                    Spacer(
                        modifier = Modifier
                            .statusBarsHeight()
                            .alpha(alpha)
                            .background(statusBarColor())
                            .fillMaxWidth()
                    )
                }
            },
            header = {
                UserInfo(viewModel = viewModel)
            },
            content = {
                val state = rememberPagerState(maxPage = tabs.lastIndex)
                Column {
                    val scope = rememberCoroutineScope()
                    IconTabsComponent(
                        items = tabs.map { it.icon to it.title },
                        selectedItem = state.currentPage,
                        onItemSelected = {
                            scope.launch {
                                state.selectPage { state.currentPage = it }
                            }
                        },
                    )
                    Pager(
                        modifier = Modifier.weight(1f),
                        state = state,
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            tabs[page].compose.invoke()
                        }
                    }
                }
            }
        )
    }
}

data class UserTabComponent(
    val icon: Painter,
    val title: String,
    val compose: @Composable () -> Unit,
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserStatusTimeline(
    userKey: MicroBlogKey,
    viewModel: UserViewModel,
) {
    val user by viewModel.user.observeAsState(initial = null)
    val account = LocalActiveAccount.current ?: return
    var excludeReplies by rememberSaveable { mutableStateOf(false) }
    val timelineViewModel =
        assistedViewModel<UserTimelineViewModel.AssistedFactory, UserTimelineViewModel>(
            account,
            userKey,
            excludeReplies,
        ) {
            it.create(account, userKey = userKey, excludeReplies)
        }
    val timelineSource = timelineViewModel.source.collectAsLazyPagingItems()
    // FIXME: 2021/2/20 Recover the scroll position require visiting the loadState once, have no idea why
    @Suppress("UNUSED_VARIABLE")
    timelineSource.loadState
    LazyUiStatusList(
        items = timelineSource,
        header = {
            user?.let { user ->
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(modifier = Modifier.width(standardPadding * 2))
                        Text(
                            modifier = Modifier.weight(1f),
                            text = if (user.statusesCount > 1) {
                                stringResource(
                                    id = R.string.common_countable_tweet_single,
                                    user.statusesCount
                                )
                            } else {
                                stringResource(
                                    id = R.string.common_countable_tweet_multiple,
                                    user.statusesCount
                                )
                            }
                        )
                        Box {
                            var showDropdown by remember {
                                mutableStateOf(false)
                            }
                            DropdownMenu(
                                expanded = showDropdown,
                                onDismissRequest = { showDropdown = false }
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        excludeReplies = false
                                        showDropdown = false
                                    }
                                ) {
                                    ListItem(
                                        icon = {
                                            RadioButton(
                                                selected = !excludeReplies,
                                                onClick = {
                                                    excludeReplies = false
                                                    showDropdown = false
                                                },
                                            )
                                        }
                                    ) {
                                        Text(text = stringResource(id = R.string.scene_profile_filter_all))
                                    }
                                }
                                DropdownMenuItem(
                                    onClick = {
                                        excludeReplies = true
                                        showDropdown = false
                                    }
                                ) {
                                    ListItem(
                                        icon = {
                                            RadioButton(
                                                selected = excludeReplies,
                                                onClick = {
                                                    excludeReplies = true
                                                    showDropdown = false
                                                },
                                            )
                                        }
                                    ) {
                                        Text(text = stringResource(id = R.string.scene_profile_filter_exclude_replies))
                                    }
                                }
                            }
                            IconButton(
                                onClick = {
                                    showDropdown = !showDropdown
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_filter),
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                }
                item {
                    Divider(
                        modifier = Modifier.alpha(0.08f)
                    )
                }
            }
        },
    )
}

@Composable
fun UserMediaTimeline(
    userKey: MicroBlogKey,
) {
    val account = LocalActiveAccount.current ?: return
    val mediaViewModel =
        assistedViewModel<UserMediaTimelineViewModel.AssistedFactory, UserMediaTimelineViewModel>(
            account,
            userKey,
        ) {
            it.create(account, userKey = userKey)
        }
    val mediaSource = mediaViewModel.source.collectAsLazyPagingItems()
    // FIXME: 2021/2/20 Recover the scroll position require visiting the loadState once, have no idea why
    @Suppress("UNUSED_VARIABLE")
    mediaSource.loadState
    if (mediaSource.itemCount > 0) {
        LazyColumn2 {
            item {
                Box(modifier = Modifier.height(standardPadding))
            }
            itemsPagingGridIndexed(
                mediaSource,
                rowSize = 2,
                spacing = standardPadding,
                padding = standardPadding
            ) { index, pair ->
                pair?.let { item ->
                    val navigator = LocalNavigator.current
                    CompositionLocalProvider(
                        LocalVideoPlayback provides DisplayPreferences.AutoPlayback.Off,
                    ) {
                        StatusMediaPreviewItem(
                            item.first,
                            modifier = Modifier
                                .aspectRatio(1F)
                                .clip(
                                    MaterialTheme.shapes.medium
                                ),
                            onClick = {
                                navigator.media(item.second.statusKey, index)
                            }
                        )
                    }
                }
            }
            item {
                Box(modifier = Modifier.height(standardPadding))
            }
        }
    }
}

@Composable
fun UserFavouriteTimeline(
    userKey: MicroBlogKey,
) {
    val account = LocalActiveAccount.current ?: return
    val timelineViewModel =
        assistedViewModel<UserFavouriteTimelineViewModel.AssistedFactory, UserFavouriteTimelineViewModel>(
            account,
            userKey,
        ) {
            it.create(account, userKey = userKey)
        }
    val timelineSource = timelineViewModel.source.collectAsLazyPagingItems()
    // FIXME: 2021/2/20 Recover the scroll position require visiting the loadState once, have no idea why
    @Suppress("UNUSED_VARIABLE")
    timelineSource.loadState
    LazyUiStatusList(
        items = timelineSource,
    )
}

val maxBannerSize = 200.dp

@Composable
private fun UserInfo(
    viewModel: UserViewModel,
) {
    val user by viewModel.user.observeAsState()
    val navController = LocalNavController.current
    Box(
        modifier = Modifier
            .background(MaterialTheme.colors.surface.withElevation())
    ) {
        // TODO: parallax effect
        user?.profileBackgroundImage?.let {
            UserBanner(navController, it)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoxWithConstraints {
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
                contentAlignment = Alignment.Center
            ) {
                Spacer(
                    modifier = Modifier
                        .size(80.dp)
                        .withAvatarClip()
                        .clipToBounds()
                        .background(MaterialTheme.colors.surface.withElevation())
                )
                user?.let { user ->
                    UserAvatar(
                        user = user,
                        size = 72.dp
                    ) {
                        if (user.profileImage is String) {
                            navController.navigate(Route.Media.Raw(user.profileImage))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(standardPadding))
            user?.let { user ->
                Row(
                    modifier = Modifier.padding(horizontal = standardPadding * 2)
                ) {
                    if (user.platformType == PlatformType.Mastodon && user.mastodonExtra?.locked == true) {
                        CompositionLocalProvider(
                            LocalContentAlpha provides ContentAlpha.medium,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_lock),
                                contentDescription = null
                            )
                        }
                        Spacer(modifier = Modifier.width(standardPadding / 2))
                    }
                    UserName(
                        user = user,
                        style = MaterialTheme.typography.h6,
                        maxLines = Int.MAX_VALUE,
                        textAlign = TextAlign.Center
                    )
                }
                UserScreenName(user = user)
            }
            if (!viewModel.isMe) {
                Spacer(modifier = Modifier.height(standardPadding))
                UserRelationship(viewModel)
            }
            Spacer(modifier = Modifier.height(standardPadding))
            user?.let { user ->
                UserDescText(
                    modifier = Modifier.padding(horizontal = standardPadding * 2),
                    htmlDesc = user.htmlDesc,
                    url = user.twitterExtra?.url ?: emptyList(),
                )
                Spacer(modifier = Modifier.height(standardPadding))
            }
            user?.website?.let {
                val navigator = LocalNavigator.current
                ProfileItem(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                navigator.openLink(it)
                            }
                        )
                        .padding(vertical = standardPadding)
                        .fillMaxWidth(),
                    painter = painterResource(id = R.drawable.ic_globe),
                    contentDescription = stringResource(
                        id = R.string.accessibility_scene_user_website
                    ),
                    text = it,
                    textColor = MaterialTheme.colors.primary,
                )
                Spacer(modifier = Modifier.height(standardPadding))
            }
            user?.location?.takeIf { it.isNotEmpty() }?.let {
                ProfileItem(
                    painter = painterResource(id = R.drawable.ic_map_pin),
                    contentDescription = stringResource(
                        id = R.string.accessibility_scene_user_location
                    ),
                    text = it
                )
                Spacer(modifier = Modifier.height(standardPadding))
            }
            user?.let {
                MastodonUserField(it)
            }
            Spacer(modifier = Modifier.height(standardPadding))
            user?.let { UserMetrics(it) }
            Spacer(modifier = Modifier.height(standardPadding))
        }
    }
}

@Composable
fun MastodonUserField(user: UiUser) {
    if (user.platformType != PlatformType.Mastodon || user.mastodonExtra == null) {
        return
    }
    user.mastodonExtra.fields.forEachIndexed { index, field ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = standardPadding * 2)
        ) {
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.medium
            ) {
                field.name?.let { Text(text = it) }
            }
            Spacer(modifier = Modifier.width(standardPadding))
            field.value?.let {
                HtmlText(
                    htmlText = it,
                )
            }
        }
        if (index != user.mastodonExtra.fields.lastIndex) {
            Spacer(modifier = Modifier.height(standardPadding))
        }
    }
}

@Composable
private fun ProfileItem(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String?,
    text: String,
    textColor: Color = Color.Unspecified,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.height(standardPadding))
        Row(
            modifier = Modifier
                .padding(horizontal = standardPadding * 2),
        ) {
            Icon(
                painter = painter,
                contentDescription = contentDescription
            )
            Spacer(modifier = Modifier.width(standardPadding))
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor,
            )
        }
        Spacer(modifier = Modifier.height(standardPadding))
    }
}

@Composable
private fun UserRelationship(viewModel: UserViewModel) {
    val relationship by viewModel.relationship.observeAsState(initial = null)
    val loadingRelationship by viewModel.loadingRelationship.observeAsState(initial = false)
    val shape = RoundedCornerShape(percent = 50)
    relationship?.takeIf { !loadingRelationship }?.let { relationshipResult ->
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 50))
                .let {
                    if (relationshipResult.followedBy) {
                        it
                    } else {
                        it.border(
                            1.dp,
                            MaterialTheme.colors.primary,
                            shape = shape,
                        )
                    }
                }
                .clip(shape)
                .clickable {
                    if (relationshipResult.followedBy) {
                        viewModel.unfollow()
                    } else {
                        viewModel.follow()
                    }
                },
            contentColor = if (relationshipResult.followedBy) {
                contentColorFor(backgroundColor = MaterialTheme.colors.primary)
            } else {
                MaterialTheme.colors.primary
            },
            color = if (relationshipResult.followedBy) {
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.background
            }
        ) {
            Text(
                modifier = Modifier
                    .padding(ButtonDefaults.ContentPadding),
                text = if (relationshipResult.followedBy) {
                    stringResource(id = R.string.common_controls_friendship_actions_unfollow)
                } else {
                    stringResource(id = R.string.common_controls_friendship_actions_follow)
                },
            )
        }
        Spacer(modifier = Modifier.height(standardPadding / 2))
        if (relationshipResult.following) {
            Text(
                text = stringResource(id = R.string.common_controls_friendship_follows_you),
                style = MaterialTheme.typography.caption,
            )
        }
    } ?: run {
        CircularProgressIndicator()
    }
}

@Composable
private fun UserBanner(
    navController: NavController,
    bannerUrl: String
) {
    Box(
        modifier = Modifier
            .heightIn(max = maxBannerSize)
            .clickable(
                onClick = {
                    navController.navigate(Route.Media.Raw(bannerUrl))
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            )
    ) {
        NetworkImage(
            data = bannerUrl,
        )
    }
}

@Composable
fun UserMetrics(
    user: UiUser,
) {
    val navController = LocalNavController.current
    Row {
        MetricsItem(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    navController.navigate(Route.Following(user.userKey))
                },
            primaryText = user.friendsCount.toString(),
            secondaryText = stringResource(id = R.string.common_controls_profile_dashboard_following),
        )

        MetricsItem(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    navController.navigate(Route.Followers(user.userKey))
                },
            primaryText = user.followersCount.toString(),
            secondaryText = stringResource(id = R.string.common_controls_profile_dashboard_followers),
        )
        if (user.platformType == PlatformType.Twitter) {
            MetricsItem(
                modifier = Modifier
                    .weight(1f),
                primaryText = user.listedCount.toString(),
                secondaryText = stringResource(id = R.string.common_controls_profile_dashboard_listed),
            )
        }
    }
}

@Composable
fun MetricsItem(
    modifier: Modifier = Modifier,
    primaryText: String,
    secondaryText: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = primaryText)
        Text(text = secondaryText)
    }
}

@Composable
fun UserDescText(
    modifier: Modifier = Modifier,
    htmlDesc: String,
    url: List<TwitterUrlEntity>,
) {
    key(
        htmlDesc,
        url,
    ) {
        HtmlText(
            modifier = modifier,
            htmlText = htmlDesc,
            linkResolver = { href ->
                val entity = url.firstOrNull { it.url == href }
                if (entity != null) {
                    ResolvedLink(
                        expanded = entity.expandedUrl,
                        display = entity.displayUrl,
                    )
                } else if (!href.startsWith(twidereXSchema)) {
                    ResolvedLink(expanded = href)
                } else {
                    ResolvedLink(expanded = null)
                }
            }
        )
    }
}
