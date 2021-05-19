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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.HorizontalDivider
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.ui.LazyUiStatusImageList
import com.twidere.twiderex.component.lazy.ui.LazyUiStatusList
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.status.HtmlText
import com.twidere.twiderex.component.status.ResolvedLink
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
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.viewmodel.user.UserFavouriteTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserMediaTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserTimelineViewModel
import com.twidere.twiderex.viewmodel.user.UserViewModel
import kotlinx.coroutines.launch
import moe.tlaster.nestedscrollview.VerticalNestedScrollView
import moe.tlaster.nestedscrollview.rememberNestedScrollViewState
import moe.tlaster.placeholder.Placeholder
import moe.tlaster.precompose.navigation.NavController
import kotlin.math.abs

@OptIn(ExperimentalPagerApi::class)
@Composable
fun UserComponent(
    userKey: MicroBlogKey,
    tabTopPadding: PaddingValues = PaddingValues(0.dp)
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
        val nestedScrollViewState = rememberNestedScrollViewState()
        val tabTop = tabTopPadding.calculateTopPadding().value * abs(nestedScrollViewState.offset / nestedScrollViewState.maxOffset)
        VerticalNestedScrollView(
            state = nestedScrollViewState,
            header = {
                UserInfo(viewModel = viewModel)
            },
            content = {
                val pagerState = rememberPagerState(pageCount = tabs.size)
                Column(modifier = Modifier.padding(top = tabTop.toInt().dp)) {
                    val scope = rememberCoroutineScope()
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
                                        Icon(painter = item.icon, contentDescription = item.title)
                                    }
                                },
                            )
                        }
                    }
                    HorizontalPager(
                        modifier = Modifier.weight(1f),
                        state = pagerState,
                    ) { page ->
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
                    UserStatusTimelineFilter(user, excludeReplies) {
                        excludeReplies = it
                    }
                }
            }
        },
    )
}

@ExperimentalMaterialApi
@Composable
private fun UserStatusTimelineFilter(
    user: UiUser,
    excludeReplies: Boolean,
    setExcludeReplies: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.background(LocalContentColor.current.copy(alpha = 0.04f)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(UserStatusTimelineFilterDefaults.StartSpacing))
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
                        setExcludeReplies(false)
                        showDropdown = false
                    }
                ) {
                    ListItem(
                        icon = {
                            RadioButton(
                                selected = !excludeReplies,
                                onClick = {
                                    setExcludeReplies(false)
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
                        setExcludeReplies(true)
                        showDropdown = false
                    }
                ) {
                    ListItem(
                        icon = {
                            RadioButton(
                                selected = excludeReplies,
                                onClick = {
                                    setExcludeReplies(true)
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

private object UserStatusTimelineFilterDefaults {
    val StartSpacing = 16.dp
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
    LazyUiStatusImageList(mediaSource)
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
fun UserInfo(
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
                            maxWidth * UserInfoDefaults.BannerAspectRatio - UserInfoDefaults.AvatarSize / 2,
                            maxBannerSize - UserInfoDefaults.AvatarSize / 2
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
                        .size(UserInfoDefaults.AvatarSize + UserInfoDefaults.AvatarSpacing)
                        .withAvatarClip()
                        .clipToBounds()
                        .background(MaterialTheme.colors.surface.withElevation())
                )
                user?.let { user ->
                    UserAvatar(
                        user = user,
                        size = UserInfoDefaults.AvatarSize
                    ) {
                        if (user.profileImage is String) {
                            navController.navigate(Route.Media.Raw(user.profileImage))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(UserInfoDefaults.AvatarSpacing))
            user?.let { user ->
                UserInfoName(user)
            }
            if (!viewModel.isMe) {
                Spacer(modifier = Modifier.height(UserInfoDefaults.RelationshipSpacing))
                UserRelationship(viewModel)
            }
            user?.let { user ->
                UserDescText(
                    modifier = Modifier.padding(UserInfoDefaults.DescPaddingValue),
                    htmlDesc = user.htmlDesc,
                    url = user.twitterExtra?.url ?: emptyList(),
                )
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
                        .padding(UserInfoDefaults.WebsitePaddingValue)
                        .fillMaxWidth(),
                    painter = painterResource(id = R.drawable.ic_globe),
                    contentDescription = stringResource(
                        id = R.string.accessibility_scene_user_website
                    ),
                    text = it,
                    textColor = MaterialTheme.colors.primary,
                )
                Spacer(modifier = Modifier.height(UserInfoDefaults.WebsiteSpacing))
            }
            user?.location?.takeIf { it.isNotEmpty() }?.let {
                ProfileItem(
                    painter = painterResource(id = R.drawable.ic_map_pin),
                    contentDescription = stringResource(
                        id = R.string.accessibility_scene_user_location
                    ),
                    text = it
                )
                Spacer(modifier = Modifier.height(UserInfoDefaults.LocationSpacing))
            }
            user?.let {
                MastodonUserField(it)
            }
            Spacer(modifier = Modifier.height(UserInfoDefaults.UserMetricsSpacing))
            user?.let { UserMetrics(it) }
            Spacer(modifier = Modifier.height(UserInfoDefaults.UserMetricsSpacing))
        }
    }
}

object UserInfoDefaults {
    val AvatarSize = 88.dp
    val AvatarSpacing = 8.dp
    val RelationshipSpacing = 8.dp
    val WebsiteSpacing = 8.dp
    val WebsitePaddingValue = PaddingValues(
        horizontal = 0.dp,
        vertical = 8.dp
    )
    val LocationSpacing = 8.dp
    val DescPaddingValue = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    )
    val UserMetricsSpacing = 8.dp
    const val BannerAspectRatio = 160f / 320f
}

@Composable
private fun UserInfoName(user: UiUser) {
    Row(
        modifier = Modifier.padding(UserInfoNameDefaults.ContentPadding)
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
            Spacer(modifier = Modifier.width(UserInfoNameDefaults.IconSpacing))
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

private object UserInfoNameDefaults {
    val ContentPadding = PaddingValues(
        horizontal = 16.dp,
        vertical = 0.dp
    )
    val IconSpacing = 4.dp
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
                .padding(MastodonUserFieldDefaults.ContentPadding)
        ) {
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.medium
            ) {
                field.name?.let { Text(text = it) }
            }
            Spacer(modifier = Modifier.width(MastodonUserFieldDefaults.NameSpacing))
            field.value?.let {
                HtmlText(
                    htmlText = it,
                )
            }
        }
        if (index != user.mastodonExtra.fields.lastIndex) {
            Spacer(modifier = Modifier.height(MastodonUserFieldDefaults.ItemSpacing))
        }
    }
}

object MastodonUserFieldDefaults {
    val ContentPadding = PaddingValues(
        horizontal = 16.dp,
        vertical = 0.dp
    )
    val NameSpacing = 8.dp
    val ItemSpacing = 8.dp
}

@Composable
private fun ProfileItem(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String?,
    text: String,
    textColor: Color = Color.Unspecified,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(ProfileItemDefaults.ContentPadding),
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription
        )
        Spacer(modifier = Modifier.width(ProfileItemDefaults.IconSpacing))
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = textColor,
        )
    }
}

private object ProfileItemDefaults {
    val ContentPadding = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    )
    val IconSpacing = 8.dp
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
        Spacer(modifier = Modifier.height(UserRelationshipDefaults.FollowingSpacing))
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

private object UserRelationshipDefaults {
    val FollowingSpacing = 4.dp
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
            modifier = Modifier.fillMaxSize(),
            data = bannerUrl,
            placeholder = {
                Placeholder(modifier = Modifier.fillMaxSize())
            }
        )
    }
}

@Composable
fun UserMetrics(
    user: UiUser,
) {
    val navController = LocalNavController.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MetricsItem(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    navController.navigate(Route.Following(user.userKey))
                },
            primaryText = user.friendsCount.toString(),
            secondaryText = stringResource(id = R.string.common_controls_profile_dashboard_following),
        )
        HorizontalDivider(
            modifier = Modifier.height(LocalTextStyle.current.fontSize.value.dp * 2)
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
            HorizontalDivider(
                modifier = Modifier.height(LocalTextStyle.current.fontSize.value.dp * 2)
            )
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
