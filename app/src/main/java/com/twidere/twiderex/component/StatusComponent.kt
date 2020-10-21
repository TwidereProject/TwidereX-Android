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
package com.twidere.twiderex.component

import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.Icon
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.extensions.NavControllerAmbient
import com.twidere.twiderex.extensions.humanizedTimestamp
import com.twidere.twiderex.fragment.StatusFragmentArgs
import com.twidere.twiderex.fragment.UserFragmentArgs
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.mediumEmphasisContentContentColor
import com.twidere.twiderex.ui.standardPadding

@Composable
fun ExpandedStatusComponent(
    status: UiStatus,
    showInfo: Boolean = true,
    showActions: Boolean = true,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {})
            .padding(
                start = standardPadding * 2,
                top = standardPadding * 2,
                end = standardPadding * 2
            ),
    ) {
        val data = (status.retweet ?: status)
        if (status.retweet != null) {
            RetweetHeader(data = status)
            Spacer(modifier = Modifier.height(standardPadding))
        }
        StatusComponent(
            status = data,
            showActions = true,
        )

        if (showInfo) {
            if (!data.placeString.isNullOrEmpty()) {
                Row {
                    Icon(asset = Icons.Default.Place)
                    Text(text = data.placeString)
                }
            }

            Spacer(modifier = Modifier.height(standardPadding))

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = data.timestamp.humanizedTimestamp(),
                    color = mediumEmphasisContentContentColor
                )
                Spacer(modifier = Modifier.width(standardPadding))
                Text(
                    text = data.source,
                    color = mediumEmphasisContentContentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (data.replyCount > 0 || data.retweetCount > 0 || data.likeCount > 0) {
                Spacer(modifier = Modifier.height(standardPadding))
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    if (data.replyCount > 0) {
                        StatusStatistics(count = data.replyCount.toString(), text = "reply")
                        Spacer(modifier = Modifier.width(standardPadding * 2))
                    }
                    if (data.retweetCount > 0) {
                        StatusStatistics(
                            count = data.retweetCount.toString(),
                            text = "retweets"
                        )
                        Spacer(modifier = Modifier.width(standardPadding * 2))
                    }
                    if (data.likeCount > 0) {
                        StatusStatistics(count = data.likeCount.toString(), text = "likes")
                    }
                }
            }
        }

        if (showActions) {
            ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    ActionIconButton(onClick = {}) {
                        Icon(asset = Icons.Default.Reply)
                    }
                    ActionIconButton(onClick = {}) {
                        Icon(asset = Icons.Default.Comment)
                    }
                    ActionIconButton(onClick = {}) {
                        Icon(asset = Icons.Default.Favorite)
                    }
                    ActionIconButton(onClick = {}) {
                        Icon(asset = Icons.Default.Share)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StatusComponent(
    status: UiStatus,
    modifier: Modifier = Modifier,
    showActions: Boolean = true,
) {
    Box(modifier = modifier) {
        Column {
            val navController = NavControllerAmbient.current
            Row(
                modifier = Modifier.clickable(
                    onClick = {
                        navController.navigate(
                            R.id.user_fragment,
                            UserFragmentArgs(status.user).toBundle()
                        )
                    }
                )
            ) {
                UserAvatar(user = status.user)
                Spacer(modifier = Modifier.width(standardPadding))
                Column {
                    Text(
                        text = status.user.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0XFF4C9EEB)
                    )
                    Text(
                        text = "@${status.user.screenName}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = AmbientEmphasisLevels.current.medium.applyEmphasis(
                            AmbientContentColor.current
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(standardPadding))

            Text(text = status.text)

            if (status.media.any()) {
                Spacer(modifier = Modifier.height(standardPadding))
                StatusMediaComponent(
                    status = status,
                )
            }

            if (status.quote != null) {
                Spacer(modifier = Modifier.height(standardPadding))
                Box(
                    modifier = Modifier
                        .border(
                            1.dp,
                            AmbientContentColor.current.copy(alpha = 0.12f),
                            RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    StatusComponent(
                        status = status.quote,
                        showActions = false,
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    navController.navigate(
                                        R.id.status_fragment,
                                        StatusFragmentArgs(
                                            status = status.quote,
                                        ).toBundle()
                                    )
                                }
                            )
                            .padding(standardPadding),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusStatistics(
    count: String,
    text: String,
) {
    Row {
        Text(text = count)
        Spacer(modifier = Modifier.width(standardPadding))
        Text(text = text, color = mediumEmphasisContentContentColor)
    }
}
