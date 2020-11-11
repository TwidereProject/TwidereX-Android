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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.twidere.twiderex.R
import com.twidere.twiderex.extensions.humanizedTimestamp
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.AmbientNavController
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
        )

        if (showInfo) {
            if (!data.placeString.isNullOrEmpty()) {
                Row {
                    Icon(asset = vectorResource(id = R.drawable.ic_map_pin))
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
                    ReplyButton(status = status, withNumber = false)
                    RetweetButton(status = status, withNumber = false)
                    LikeButton(status = status, withNumber = false)
                    ShareButton(status = status)
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
) {
    Box(modifier = modifier) {
        Column {
            val navController = AmbientNavController.current
            Row(
                modifier = Modifier.clickable(
                    onClick = {
                        navController.navigate("user/${status.user.screenName}")
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
                        color = MaterialTheme.colors.primary,
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
                            MaterialTheme.shapes.medium,
                        )
                        .clip(MaterialTheme.shapes.medium)
                ) {
                    StatusComponent(
                        status = status.quote,
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    navController.navigate("status/${status.quote.statusId}")
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
