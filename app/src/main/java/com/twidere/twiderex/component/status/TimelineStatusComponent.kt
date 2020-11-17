/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.AmbientContentColor
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.navigation.AmbientNavigator
import com.twidere.twiderex.extensions.humanizedTimestamp
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.preferences.AmbientDisplayPreferences
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding

@Composable
fun TimelineStatusComponent(
    data: UiStatus,
    showActions: Boolean = true,
) {
    val navigator = AmbientNavigator.current
    Column {
        val status = (data.retweet ?: data)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        navigator.status(data.statusId)
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
                showActions = showActions,
            )
            if (showActions) {
                Providers(
                    AmbientContentAlpha provides ContentAlpha.medium
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

@Composable
private fun StatusComponent(
    status: UiStatus,
    modifier: Modifier = Modifier,
    showActions: Boolean = true,
) {
    val navigator = AmbientNavigator.current
    val isMediaPreviewEnabled = AmbientDisplayPreferences.current.mediaPreview
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
                        color = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Providers(
                        AmbientContentAlpha provides ContentAlpha.medium
                    ) {
                        Text(
                            text = "@${status.user.screenName}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Row {
                    Text(text = status.timestamp.humanizedTimestamp())
                    if (showActions) {
                        Icon(
                            asset = Icons.Default.ArrowDropDown,
                            modifier = Modifier
                                .clickable(
                                    onClick = {},
                                ),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            StatusText(status = status)

            if (status.media.any() && isMediaPreviewEnabled) {
                Spacer(modifier = Modifier.height(standardPadding))
                StatusMediaComponent(
                    status = status,
                )
            }

            if (!status.placeString.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(standardPadding))
                Providers(
                    AmbientContentAlpha provides ContentAlpha.medium
                ) {
                    Row {
                        Icon(asset = vectorResource(id = R.drawable.ic_map_pin))
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
                            AmbientContentColor.current.copy(alpha = 0.12f),
                            MaterialTheme.shapes.medium
                        )
                        .clip(MaterialTheme.shapes.medium)
                ) {
                    StatusComponent(
                        status = status.quote,
                        showActions = false,
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    navigator.status(statusId = status.quote.statusId)
                                }
                            )
                            .padding(standardPadding),
                    )
                }
            }
        }
    }
}
