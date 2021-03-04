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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.twidere.twiderex.R
import com.twidere.twiderex.component.HumanizedTime
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.mediumEmphasisContentContentColor
import com.twidere.twiderex.ui.standardPadding

@Composable
fun ExpandedStatusComponent(
    data: UiStatus,
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
        StatusContent(
            modifier = Modifier.fillMaxWidth(),
            data = data,
            type = StatusContentType.Extend,
        )

        val status = (data.retweet ?: data)
        if (showInfo) {
            if (!status.placeString.isNullOrEmpty()) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_map_pin),
                        contentDescription = stringResource(
                            id = R.string.accessibility_common_status_location
                        )
                    )
                    Text(text = status.placeString)
                }
            }

            Spacer(modifier = Modifier.height(standardPadding))

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.medium
                ) {
                    HumanizedTime(time = status.timestamp)
                    Spacer(modifier = Modifier.width(standardPadding))
                    Text(
                        text = status.source,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            if (status.replyCount > 0 || status.retweetCount > 0 || status.likeCount > 0) {
                Spacer(modifier = Modifier.height(standardPadding))
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    if (status.replyCount > 0) {
                        StatusStatistics(
                            count = status.replyCount,
                            text = R.string.scene_status_reply_mutiple,
                        )
                        Spacer(modifier = Modifier.width(standardPadding * 2))
                    }
                    if (status.retweetCount > 0) {
                        StatusStatistics(
                            count = status.retweetCount,
                            text = R.string.scene_status_retweet_mutiple,
                        )
                        Spacer(modifier = Modifier.width(standardPadding * 2))
                    }
                    if (status.likeCount > 0) {
                        StatusStatistics(
                            count = status.likeCount,
                            text = R.string.scene_status_like_multiple,
                        )
                    }
                }
            }
        }

        if (showActions) {
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.medium
            ) {
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
private fun StatusStatistics(
    count: Long,
    text: Int,
) {
    Text(
        text = stringResource(id = text, count),
        color = mediumEmphasisContentContentColor
    )
}
