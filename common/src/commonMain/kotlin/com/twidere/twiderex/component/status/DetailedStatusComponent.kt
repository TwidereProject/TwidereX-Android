/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.FormattedTime
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.extensions.humanizedCount
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiStatus

@Composable
fun DetailedStatusComponent(
    data: UiStatus,
    showInfo: Boolean = true,
    showActions: Boolean = true,
    lineUp: Boolean = false,
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
            ),
        ) {
            StatusContent(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(top = DetailedStatusDefaults.ContentPadding),
                lineUp = lineUp,
                data = data,
                type = StatusContentType.Extend,
            )
            if (showInfo) {
                Spacer(modifier = Modifier.height(DetailedStatusDefaults.InfoContentSpacing))
                ProvideTextStyle(value = MaterialTheme.typography.caption) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.disabled
                    ) {
                        if (status.geo.name.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                Icon(
                                    modifier = Modifier.size(MaterialTheme.typography.body1.fontSize.value.dp),
                                    painter = painterResource(res = com.twidere.twiderex.MR.files.ic_map_pin),
                                    contentDescription = stringResource(
                                        res = com.twidere.twiderex.MR.strings.accessibility_common_status_location
                                    )
                                )
                                Text(text = status.geo.name)
                            }
                            Spacer(modifier = Modifier.height(DetailedStatusDefaults.ContentSpacing))
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        ) {
                            FormattedTime(time = status.timestamp)
                            Spacer(modifier = Modifier.width(DetailedStatusDefaults.TimestampSpacing))
                            HtmlText(
                                htmlText = status.source,
                                maxLines = 1,
                                linkResolver = {
                                    ResolvedLink(null)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(DetailedStatusDefaults.ContentSpacing))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            StatusStatistics(
                                count = status.metrics.reply,
                                icon = painterResource(res = com.twidere.twiderex.MR.files.ic_corner_up_left),
                                contentDescription = stringResource(
                                    res = com.twidere.twiderex.MR.strings.scene_status_reply_mutiple,
                                    status.metrics.reply,
                                ),
                            )
                            Spacer(modifier = Modifier.width(DetailedStatusDefaults.StatusStatisticsSpacing))
                            StatusStatistics(
                                count = status.metrics.retweet,
                                icon = painterResource(res = com.twidere.twiderex.MR.files.ic_repeat),
                                contentDescription = stringResource(
                                    res = com.twidere.twiderex.MR.strings.scene_status_retweet_mutiple,
                                    status.metrics.retweet,
                                ),
                            )
                            if (status.platformType == PlatformType.Twitter) {
                                Spacer(modifier = Modifier.width(DetailedStatusDefaults.StatusStatisticsSpacing))
                                StatusStatistics(
                                    count = status.twitterExtra?.quoteCount ?: 0,
                                    icon = painterResource(res = com.twidere.twiderex.MR.files.ic_blockquote),
                                    contentDescription = null,
                                )
                            }
                            Spacer(modifier = Modifier.width(DetailedStatusDefaults.StatusStatisticsSpacing))
                            StatusStatistics(
                                count = status.metrics.like,
                                icon = painterResource(res = com.twidere.twiderex.MR.files.ic_heart),
                                contentDescription = stringResource(
                                    res = com.twidere.twiderex.MR.strings.scene_status_like_multiple,
                                    status.metrics.like,
                                ),
                            )
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
                    ReplyButton(status = data, withNumber = false)
                    RetweetButton(status = data, withNumber = false)
                    LikeButton(status = data, withNumber = false)
                    ShareButton(status = data)
                }
            }
        }
    }
}

object DetailedStatusDefaults {
    val ContentPadding = 16.dp
    val StatusStatisticsSpacing = 32.dp
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
        Text(text = count.humanizedCount())
    }
}

private object StatusStatisticsDefaults {
    val IconSpacing = 4.dp
}
