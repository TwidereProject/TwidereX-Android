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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.HumanizedTime
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.preferences.LocalDisplayPreferences
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.ui.statusActionIconSize

@Composable
fun TimelineStatusComponent(
    data: UiStatus,
    showActions: Boolean = true,
) {
    val navigator = LocalNavigator.current
    Column {
        val status = (data.retweet ?: data)
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
                    top = standardPadding * 2,
                    end = standardPadding * 2
                ),
        ) {
            if (data.retweet != null) {
                RetweetHeader(data = data)
                Spacer(modifier = Modifier.height(standardPadding))
            }
            StatusComponent(
                status = status,
            )
            if (showActions) {
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.medium
                ) {
                    Spacer(modifier = Modifier.height(standardPadding))
                    Row {
                        Spacer(modifier = Modifier.width(profileImageSize))
                        ReplyButton(status = status)
                        RetweetButton(status = status)
                        LikeButton(status = status)
                        ShareButton(status = status, compat = true)
                    }
                }
            }
            Spacer(modifier = Modifier.height(standardPadding))
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StatusComponent(
    status: UiStatus,
    modifier: Modifier = Modifier,
) {
    val navigator = LocalNavigator.current
    Row(modifier = modifier) {
        UserAvatar(user = status.user)
        Spacer(modifier = Modifier.width(standardPadding))
        Column {
            Row {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = status.user.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.medium
                    ) {
                        Text(
                            text = "@${status.user.screenName}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                HumanizedTime(time = status.timestamp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            StatusText(status)

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

            if (status.quote != null) {
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
                    StatusComponent(
                        status = status.quote,
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    navigator.status(statusKey = status.quote.statusKey)
                                }
                            )
                            .padding(standardPadding),
                    )
                }
            }
        }
    }
}
