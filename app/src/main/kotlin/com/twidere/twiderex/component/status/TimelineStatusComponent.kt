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
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twidere.services.mastodon.model.Option
import com.twidere.services.mastodon.model.Poll
import com.twidere.services.mastodon.model.Visibility
import com.twidere.twiderex.R
import com.twidere.twiderex.component.HumanizedTime
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.extensions.humanizedTimestamp
import com.twidere.twiderex.model.MastodonStatusType
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.preferences.LocalDisplayPreferences
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.ui.statusActionIconSize
import kotlin.math.max

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
        }
        else -> NormalStatus(data, showActions)
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
                    navigator.status(data.statusKey)
                }
            )
            .padding(
                start = standardPadding * 2,
                end = standardPadding * 2
            ),
    ) {
        Spacer(modifier = Modifier.height(standardPadding))
        StatusContent(
            data = data,
        )
        if (showActions) {
            Row {
                Spacer(modifier = Modifier.width(profileImageSize + standardPadding))
                val status = (data.retweet ?: data)
                StatusActions(status)
            }
        }
    }
}

@Composable
private fun StatusHeader(data: UiStatus) {
    if (data.retweet != null) {
        RetweetHeader(data = data)
        Spacer(modifier = Modifier.height(standardPadding))
    }
}

@Composable
private fun StatusActions(status: UiStatus) {
    CompositionLocalProvider(
        LocalContentAlpha provides ContentAlpha.medium
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
                                painter = when (status.mastodonExtra.visibility) {
                                    Visibility.Public -> painterResource(id = R.drawable.ic_globe)
                                    Visibility.Unlisted -> rememberVectorPainter(image = Icons.Default.List)
                                    Visibility.Private -> rememberVectorPainter(image = Icons.Default.Lock)
                                    Visibility.Direct -> rememberVectorPainter(image = Icons.Default.Message)
                                },
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

    if (status.platformType == PlatformType.Mastodon && status.mastodonExtra?.poll != null) {
        Spacer(modifier = Modifier.height(standardPadding))
        MastodonPoll(status)
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
                            navigator.status(statusKey = quote.statusKey)
                        }
                    )
                    .padding(standardPadding),
                type = type,
            )
        }
    }
}

@Composable
fun MastodonPoll(status: UiStatus) {
    if (status.platformType != PlatformType.Mastodon || status.mastodonExtra?.poll == null) {
        return
    }

    status.mastodonExtra.poll.options?.forEachIndexed { index, option ->
        MastodonPollOption(option, index, status.mastodonExtra.poll)
        if (index != status.mastodonExtra.poll.options?.lastIndex) {
            Spacer(modifier = Modifier.height(standardPadding))
        }
    }

    Spacer(modifier = Modifier.height(standardPadding))
    Row {
        if (status.mastodonExtra.poll.votersCount != null) {
            Text(text = "${status.mastodonExtra.poll.votersCount} people")
        } else if (status.mastodonExtra.poll.votesCount != null) {
            Text(text = "${status.mastodonExtra.poll.votesCount} votes")
        }
        Spacer(modifier = Modifier.width(standardPadding))
        if (status.mastodonExtra.poll.expired == true) {
            Text(text = "Closed")
        } else {
            Text(text = status.mastodonExtra.poll.expiresAt?.time?.humanizedTimestamp() ?: "")
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MastodonPollOption(
    option: Option,
    index: Int,
    poll: Poll,
) {
    val size = LocalTextStyle.current.fontSize.value.dp + standardPadding * 3
    val transition = updateTransition(targetState = option.votesCount)
    val progress by transition.animateFloat {
        (option.votesCount ?: 0).toFloat() / max((poll.votesCount ?: 0), 1).toFloat()
    }
    val color = MaterialTheme.colors.onBackground
    Box(
        modifier = Modifier
            .clip(
                if (poll.multiple == true) {
                    RoundedCornerShape(4.dp)
                } else {
                    RoundedCornerShape(percent = 50)
                }
            ).let {
                if (poll.voted == true) {
                    it
                } else {
                    it.clickable {
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .height(size)
                .fillMaxWidth()
                .background(color.copy(alpha = 0.08f)),
        )
        Box(
            modifier = Modifier
                .height(size)
                .fillMaxWidth(progress)
                .clip(
                    if (poll.multiple == true) {
                        RoundedCornerShape(4.dp)
                    } else {
                        RoundedCornerShape(percent = 50)
                    }
                )
                .background(
                    color.let {
                        if (poll.ownVotes?.contains(index) == true) {
                            it.copy(alpha = 0.38f)
                        } else {
                            it.copy(alpha = 0.2f)
                        }
                    }
                ),
        )
        Row(
            modifier = Modifier
                .height(size),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(standardPadding))
            Box(
                modifier = Modifier.width(LocalTextStyle.current.fontSize.value.dp + standardPadding)
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = poll.ownVotes?.contains(
                        index
                    ) == true,
                    enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                    exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut(),
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colors.surface, shape = CircleShape),
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(4.dp),
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(standardPadding))
            Text(
                modifier = Modifier.weight(1f),
                text = option.title ?: "",
            )
            Text(text = String.format("%.0f%%", progress * 100))
            Spacer(modifier = Modifier.width(standardPadding))
        }
    }
}
