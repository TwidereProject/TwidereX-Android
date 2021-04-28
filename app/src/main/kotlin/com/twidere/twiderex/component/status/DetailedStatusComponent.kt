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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.HumanizedTime
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiStatus

@Composable
fun DetailedStatusComponent(
    data: UiStatus,
    showInfo: Boolean = true,
    showActions: Boolean = true,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {}),
    ) {
        val status = (data.retweet ?: data)
        Column(
            modifier = Modifier.padding(
                start = DetailedStatusDefaults.ContentPadding,
                end = DetailedStatusDefaults.ContentPadding,
                top = DetailedStatusDefaults.ContentPadding,
            ),
        ) {
            StatusContent(
                modifier = Modifier.fillMaxWidth(),
                data = data,
                type = StatusContentType.Extend,
            )
            if (showInfo) {
                Spacer(modifier = Modifier.height(DetailedStatusDefaults.InfoContentSpacing))
                ProvideTextStyle(value = MaterialTheme.typography.caption) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.disabled
                    ) {
                        if (!status.placeString.isNullOrEmpty()) {
                            Row {
                                Icon(
                                    modifier = Modifier.size(MaterialTheme.typography.body1.fontSize.value.dp),
                                    painter = painterResource(id = R.drawable.ic_map_pin),
                                    contentDescription = stringResource(
                                        id = R.string.accessibility_common_status_location
                                    )
                                )
                                Text(text = status.placeString)
                            }
                            Spacer(modifier = Modifier.height(DetailedStatusDefaults.ContentSpacing))
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        ) {
                            HumanizedTime(time = status.timestamp)
                            Spacer(modifier = Modifier.width(DetailedStatusDefaults.TimestampSpacing))
                            Text(
                                text = status.source,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        if (status.replyCount > 0 || status.retweetCount > 0 || status.likeCount > 0) {
                            Spacer(modifier = Modifier.height(DetailedStatusDefaults.ContentSpacing))
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                StatusStatistics(
                                    count = status.replyCount,
                                    icon = painterResource(id = R.drawable.ic_corner_up_left),
                                    contentDescription = stringResource(
                                        id = R.string.scene_status_reply_mutiple,
                                        status.replyCount,
                                    ),
                                )
                                Spacer(modifier = Modifier.width(DetailedStatusDefaults.StatusStatisticsSpacing))
                                StatusStatistics(
                                    count = status.retweetCount,
                                    icon = painterResource(id = R.drawable.ic_repeat),
                                    contentDescription = stringResource(
                                        id = R.string.scene_status_retweet_mutiple,
                                        status.retweetCount,
                                    ),
                                )
                                if (status.platformType == PlatformType.Twitter) {
                                    Spacer(modifier = Modifier.width(DetailedStatusDefaults.StatusStatisticsSpacing))
                                    StatusStatistics(
                                        count = status.twitterExtra?.quoteCount ?: 0,
                                        icon = painterResource(id = R.drawable.ic_blockquote),
                                        contentDescription = null,
                                    )
                                }
                                Spacer(modifier = Modifier.width(DetailedStatusDefaults.StatusStatisticsSpacing))
                                StatusStatistics(
                                    count = status.likeCount,
                                    icon = painterResource(id = R.drawable.ic_heart),
                                    contentDescription = stringResource(
                                        id = R.string.scene_status_like_multiple,
                                        status.likeCount,
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showActions) {
            Spacer(modifier = Modifier.height(DetailedStatusDefaults.InfoContentSpacing))
            Divider(
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.06f),
                thickness = 0.5.dp
            )
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.medium
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    ReplyButton(status = status, withNumber = false)
                    RetweetButton(status = status, withNumber = false)
                    LikeButton(status = status, withNumber = false)
                    ShareButton(status = status)
                }
            }
        }
    }
}

object DetailedStatusDefaults {
    val ContentPadding = 16.dp
    val StatusStatisticsSpacing = 16.dp
    val ContentSpacing = 8.dp
    val InfoContentSpacing = 12.dp
    val TimestampSpacing = 8.dp
}

@Composable
private fun StatusStatistics(
    count: Long,
    icon: Painter,
    contentDescription: String?,
) {
    Row {
        Icon(
            modifier = Modifier.size(MaterialTheme.typography.body1.fontSize.value.dp),
            painter = icon,
            contentDescription = contentDescription,
        )
        Spacer(modifier = Modifier.width(StatusStatisticsDefaults.IconSpacing))
        Text(text = count.toString())
    }
}

private object StatusStatisticsDefaults {
    val IconSpacing = 4.dp
}
