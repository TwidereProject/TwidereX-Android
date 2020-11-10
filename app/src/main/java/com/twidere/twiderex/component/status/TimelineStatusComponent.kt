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
package com.twidere.twiderex.component.status

import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.Text
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
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.ButtonConstants
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.twidere.twiderex.extensions.humanizedTimestamp
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.providers.AmbientStatusActions
import com.twidere.twiderex.settings.AmbientMediaPreview
import com.twidere.twiderex.ui.AmbientInStoryboard
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.mediumEmphasisContentContentColor
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding

@Composable
fun TimelineStatusComponent(
    data: UiStatus,
    showActions: Boolean = true,
) {
    val inStoryBoard = AmbientInStoryboard.current
    Column {
        val status = (data.retweet ?: data)
        val navController = AmbientNavController.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        if (!inStoryBoard) {
                            navController.navigate("status/${data.statusId}")
                        }
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
                Spacer(modifier = Modifier.height(standardPadding))
                Row {
                    Spacer(modifier = Modifier.width(profileImageSize))
                    ReplyButton(status = status)
                    RetweetButton(status = status)
                    LikeButton(status = status)
                    TextButton(
                        onClick = {},
                        colors = ButtonConstants.defaultTextButtonColors(
                            contentColor = mediumEmphasisContentContentColor
                        )
                    ) {
                        Icon(
                            asset = Icons.Default.Share,
                        )
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
    val inStoryBoard = AmbientInStoryboard.current
    val isMediaPreviewEnabled = AmbientMediaPreview.current
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
                    ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
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

            Text(text = status.text)

            if (status.media.any() && isMediaPreviewEnabled) {
                Spacer(modifier = Modifier.height(standardPadding))
                StatusMediaComponent(
                    status = status,
                )
            }

            if (!status.placeString.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(standardPadding))
                ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                    Row {
                        Icon(asset = Icons.Default.Place)
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
                    val navController = AmbientNavController.current
                    StatusComponent(
                        status = status.quote,
                        showActions = false,
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    if (!inStoryBoard) {
                                        navController.navigate("status/${status.quote.statusId}")
                                    }
                                }
                            )
                            .padding(standardPadding),
                    )
                }
            }
        }
    }
}
