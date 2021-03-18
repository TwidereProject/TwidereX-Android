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
package com.twidere.twiderex.component.status

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.HumanizedTime
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.db.model.DbMastodonStatusExtra
import com.twidere.twiderex.extensions.icon
import com.twidere.twiderex.model.MastodonStatusType
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.preferences.LocalDisplayPreferences
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.ui.statusActionIconSize

@Composable
fun TimelineStatusComponent(
    data: UiStatus,
    showActions: Boolean = true,
) {
    when {
        data.platformType == PlatformType.Mastodon &&
            data.mastodonExtra != null
            && (
                data.mastodonExtra.type == MastodonStatusType.NotificationFollowRequest ||
                    data.mastodonExtra.type == MastodonStatusType.NotificationFollow
                ) -> {
            MastodonFollowStatus(data)
        }
        else -> NormalStatus(data, showActions)
    }
}

@Composable
fun MastodonFollowStatus(data: UiStatus) {
    val navigator = LocalNavigator.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigator.user(data.user)
            }
            .padding(
                horizontal = standardPadding * 2,
            ),
    ) {
        Spacer(modifier = Modifier.height(standardPadding))
        StatusHeader(data = data)
        Row {
            UserAvatar(user = data.user)
            Spacer(modifier = Modifier.width(standardPadding))
            Column {
                UserName(data.user)
                Spacer(modifier = Modifier.width(standardPadding / 2))
                UserScreenName(data.user)
            }
            Spacer(modifier = Modifier.width(standardPadding))
        }
        Spacer(modifier = Modifier.height(standardPadding))
    }
}

@Composable
private fun NormalStatus(
    data: UiStatus,
    showActions: Boolean
) {
    val navigator = LocalNavigator.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    navigator.status(data)
                },
            )
            .padding(
                horizontal = standardPadding * 2,
            ),
    ) {
        Spacer(modifier = Modifier.height(standardPadding))
        StatusContent(
            modifier = Modifier
                .padding(
                    end = standardPadding
                ),
            data = data,
        )
        if (showActions) {
            Row {
                Spacer(modifier = Modifier.width(profileImageSize))
                val status = (data.retweet ?: data)
                StatusActions(status)
            }
        } else {
            Spacer(modifier = Modifier.height(standardPadding))
        }
    }
}

@Composable
private fun StatusHeader(data: UiStatus) {
    when {
        data.platformType == PlatformType.Mastodon && data.mastodonExtra != null -> {
            MastodonStatusHeader(data.mastodonExtra, data)
            Spacer(modifier = Modifier.height(standardPadding))
        }
        data.retweet != null -> {
            RetweetHeader(data = data)
            Spacer(modifier = Modifier.height(standardPadding))
        }
    }
}

@Composable
private fun MastodonStatusHeader(
    mastodonExtra: DbMastodonStatusExtra,
    data: UiStatus
) {
    when (mastodonExtra.type) {
        MastodonStatusType.Status -> Unit
        MastodonStatusType.NotificationFollow -> {
            TweetHeader(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user_plus),
                        contentDescription = null,
                        tint = Color(0xFF4C9EEB),
                    )
                },
                text = {
                    Text(
                        text = stringResource(
                            id = R.string.common_notification_follow,
                            data.user.displayName
                        )
                    )
                }
            )
        }
        MastodonStatusType.NotificationFollowRequest -> {
            TweetHeader(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user_exclamation),
                        contentDescription = null,
                        tint = Color(0xFFFF9500),
                    )
                },
                text = {
                    Text(
                        text = stringResource(
                            id = R.string.common_notification_follow_request,
                            data.user.displayName
                        )
                    )
                }
            )
        }
        MastodonStatusType.NotificationMention -> Unit
        MastodonStatusType.NotificationReblog -> {
            RetweetHeader(data = data)
        }
        MastodonStatusType.NotificationFavourite -> {
            TweetHeader(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_heart),
                        contentDescription = null,
                        tint = Color(0xFFFF2D55),
                    )
                },
                text = {
                    Text(
                        text = stringResource(
                            id = R.string.common_notification_favourite,
                            data.user.displayName
                        )
                    )
                }
            )
        }
        MastodonStatusType.NotificationPoll -> {
            TweetHeader(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_poll),
                        contentDescription = null,
                        tint = Color(0xFF4C9EEB),
                    )
                },
                text = {
                    val text = if (LocalActiveAccount.current?.let { it.accountKey == data.user.userKey } == true) {
                        stringResource(
                            id = R.string.common_notification_own_poll,
                        )
                    } else {
                        stringResource(
                            id = R.string.common_notification_poll,
                        )
                    }

                    Text(
                        text = text
                    )
                }
            )
        }
        MastodonStatusType.NotificationStatus -> {
            TweetHeader(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bell_ringing),
                        contentDescription = null,
                        tint = Color(0xFFFF9500),
                    )
                },
                text = {
                    Text(
                        text = stringResource(
                            id = R.string.common_notification_status,
                            data.user.displayName
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun StatusActions(status: UiStatus) {
    CompositionLocalProvider(
        LocalContentAlpha provides ContentAlpha.medium,
    ) {
        Row {
            ReplyButton(status = status)
            RetweetButton(status = status)
            LikeButton(status = status)
            ShareButton(status = status, compat = true)
        }
    }
}

enum class StatusContentType {
    Normal,
    Extend,
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun StatusContent(
    modifier: Modifier = Modifier,
    data: UiStatus,
    type: StatusContentType = StatusContentType.Normal,
) {
    Column(modifier = modifier) {
        StatusHeader(data)
        val status = data.retweet ?: data
        Row {
            UserAvatar(user = status.user)
            Spacer(modifier = Modifier.width(standardPadding))
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                    ) {
                        UserName(status.user)
                        if (type == StatusContentType.Normal) {
                            Spacer(modifier = Modifier.width(standardPadding / 2))
                            UserScreenName(status.user)
                        }
                    }
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.medium
                    ) {
                        if (status.platformType == PlatformType.Mastodon && status.mastodonExtra != null) {
                            Icon(
                                modifier = Modifier.size(LocalTextStyle.current.fontSize.value.dp),
                                painter = status.mastodonExtra.visibility.icon(),
                                contentDescription = status.mastodonExtra.visibility.name
                            )
                            Spacer(modifier = Modifier.width(standardPadding / 2))
                        }
                        if (type == StatusContentType.Normal) {
                            HumanizedTime(time = status.timestamp)
                        }
                    }
                }
                when (type) {
                    StatusContentType.Normal -> {
                        Spacer(modifier = Modifier.height(standardPadding / 2))
                        StatusBody(status, type)
                    }
                    StatusContentType.Extend -> UserScreenName(status.user)
                }
            }
        }
        if (type == StatusContentType.Extend) {
            Spacer(modifier = Modifier.height(standardPadding))
            StatusBody(status = status, type = type)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ColumnScope.StatusBody(
    status: UiStatus,
    type: StatusContentType = StatusContentType.Normal,
) {
    val navigator = LocalNavigator.current

    StatusText(
        status = status,
    )

    if (status.media.any()) {
        Spacer(modifier = Modifier.height(standardPadding))
        AnimatedVisibility(visible = LocalDisplayPreferences.current.mediaPreview) {
            StatusMediaComponent(
                status = status,
            )
        }
        AnimatedVisibility(visible = !LocalDisplayPreferences.current.mediaPreview) {
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.medium
            ) {
                Row(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                navigator.media(statusKey = status.statusKey)
                            }
                        )
                        .fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_photo),
                        contentDescription = stringResource(
                            id = R.string.accessibility_common_status_media
                        )
                    )
                    Spacer(modifier = Modifier.width(standardPadding))
                    Text(text = stringResource(id = R.string.common_controls_status_media))
                }
            }
        }
    }

    if (!status.placeString.isNullOrEmpty()) {
        Spacer(modifier = Modifier.height(standardPadding))
        CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.medium
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(statusActionIconSize),
                    painter = painterResource(id = R.drawable.ic_map_pin),
                    contentDescription = stringResource(id = R.string.accessibility_common_status_location)
                )
                Box(modifier = Modifier.width(standardPadding))
                Text(text = status.placeString)
            }
        }
    }

    status.quote?.let { quote ->
        Spacer(modifier = Modifier.height(standardPadding))
        Box(
            modifier = Modifier
                .border(
                    1.dp,
                    LocalContentColor.current.copy(alpha = 0.12f),
                    MaterialTheme.shapes.medium
                )
                .clip(MaterialTheme.shapes.medium)
        ) {
            StatusContent(
                data = quote,
                modifier = Modifier
                    .clickable(
                        onClick = {
                            navigator.status(quote)
                        }
                    )
                    .padding(standardPadding),
                type = type,
            )
        }
    }
}
