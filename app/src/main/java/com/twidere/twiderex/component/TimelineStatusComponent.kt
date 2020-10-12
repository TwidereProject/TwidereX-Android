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

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope.Companion.weight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.twidere.twiderex.R
import com.twidere.twiderex.extensions.NavControllerAmbient
import com.twidere.twiderex.extensions.humanizedTimestamp
import com.twidere.twiderex.fragment.StatusFragmentArgs
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.mediumEmphasisContentContentColor
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.StatusActionsViewModel

@Composable
fun TimelineStatusComponent(
    data: UiStatus,
) {
    val actionsViewModel = viewModel<StatusActionsViewModel>()
    Column {
        val status = (data.retweet ?: data)
        val navController = NavControllerAmbient.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        navController.navigate(
                            R.id.status_fragment,
                            StatusFragmentArgs(
                                status = data,
                                quote = data.quote,
                                retweet = data.retweet
                            ).toBundle()
                        )
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
                showActions = true,
            )
            Spacer(modifier = Modifier.height(standardPadding))
            Row {
                Spacer(modifier = Modifier.width(profileImageSize))
                StatusActionButton(
                    icon = Icons.Default.Reply,
                    count = status.replyCount,
                    onClick = {},
                )
                StatusActionButton(
                    icon = Icons.Default.Comment,
                    count = status.retweetCount,
                    colored = status.retweeted,
                    color = MaterialTheme.colors.primary,
                    onClick = {
                        actionsViewModel.retweet(status)
                    },
                )
                StatusActionButton(
                    icon = Icons.Default.Favorite,
                    count = status.likeCount,
                    colored = status.liked,
                    color = Color.Red,
                    onClick = {
                        actionsViewModel.like(status)
                    },
                )
                TextButton(
                    onClick = {},
                    contentColor = mediumEmphasisContentContentColor,
                ) {
                    Icon(
                        asset = Icons.Default.Share,
                    )
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
                        color = Color(0XFF4C9EEB)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "@${status.user.screenName}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = EmphasisAmbient.current.medium.applyEmphasis(
                            contentColor()
                        ),
                    )
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

            if (status.media.any()) {
                Spacer(modifier = Modifier.height(standardPadding))
                StatusMediaComponent(
                    status = status,
                )
            }

            if (!status.placeString.isNullOrEmpty()) {
                Row {
                    Icon(asset = Icons.Default.Place)
                    Text(text = status.placeString)
                }
            }

            if (status.quote != null) {
                Spacer(modifier = Modifier.height(standardPadding))
                Box(
                    modifier = Modifier
                        .border(
                            1.dp,
                            contentColor().copy(alpha = 0.12f),
                            RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    val navController = NavControllerAmbient.current
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
                                            quote = null,
                                            retweet = null
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
private fun StatusActionButton(
    modifier: Modifier = Modifier.weight(1f),
    icon: VectorAsset,
    count: Long,
    colored: Boolean = false,
    color: Color = contentColor(),
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
    ) {
        TextButton(
            onClick = onClick,
            contentColor = contentColor(),
        ) {
            ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                Icon(
                    asset = icon,
                    tint = if (colored) {
                        color
                    } else {
                        contentColor()
                    }
                )
                if (count > 0) {
                    Box(modifier = Modifier.width(4.dp))
                    Text(text = count.toString())
                }
            }
        }
    }
}
