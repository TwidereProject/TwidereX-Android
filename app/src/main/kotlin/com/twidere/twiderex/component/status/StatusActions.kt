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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope.Companion.weight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.AmbientContentColor
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.action.AmbientStatusActions
import com.twidere.twiderex.component.foundation.ActionIconButton
import com.twidere.twiderex.component.navigation.AmbientNavigator
import com.twidere.twiderex.extensions.humanizedCount
import com.twidere.twiderex.extensions.shareText
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.scenes.ComposeType
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.mediumEmphasisContentContentColor
import com.twidere.twiderex.ui.statusActionIconSize

@Composable
fun ReplyButton(
    status: UiStatus,
    withNumber: Boolean = true,
) {
    val navigator = AmbientNavigator.current
    val icon = vectorResource(id = R.drawable.ic_corner_up_left)
    val action = {
        navigator.compose(ComposeType.Reply, statusKey = status.statusKey)
    }
    if (withNumber) {
        StatusActionButtonWithNumbers(
            icon = icon,
            color = mediumEmphasisContentContentColor,
            count = status.replyCount,
            onClick = {
                action.invoke()
            },
        )
    } else {
        ActionIconButton(
            onClick = {
                action.invoke()
            },
        ) {
            Icon(
                imageVector = icon,
                tint = mediumEmphasisContentContentColor,
            )
        }
    }
}

@Composable
fun LikeButton(
    status: UiStatus,
    withNumber: Boolean = true,
) {
    val actionsViewModel = AmbientStatusActions.current
    val account = AmbientActiveAccount.current
    val color = if (status.liked) {
        Color.Red
    } else {
        mediumEmphasisContentContentColor
    }
    val icon = vectorResource(id = R.drawable.ic_heart)
    val action = {
        if (account != null) {
            actionsViewModel.like(status, account)
        }
    }
    if (withNumber) {
        StatusActionButtonWithNumbers(
            icon = icon,
            count = status.likeCount,
            color = color,
            onClick = {
                action.invoke()
            },
        )
    } else {
        ActionIconButton(
            onClick = {
                action.invoke()
            },
        ) {
            Icon(
                imageVector = icon,
                tint = color,
            )
        }
    }
}

@Composable
fun RetweetButton(
    status: UiStatus,
    withNumber: Boolean = true,
) {
    val actionsViewModel = AmbientStatusActions.current
    val account = AmbientActiveAccount.current
    val color = if (status.retweeted) {
        MaterialTheme.colors.primary
    } else {
        mediumEmphasisContentContentColor
    }
    val icon = vectorResource(id = R.drawable.ic_repeat)
    val action = {
        if (account != null) {
            actionsViewModel.retweet(status, account)
        }
    }
    if (withNumber) {
        StatusActionButtonWithNumbers(
            icon = icon,
            count = status.retweetCount,
            color = color,
            onClick = {
                action.invoke()
            },
        )
    } else {
        ActionIconButton(
            onClick = {
                action.invoke()
            },
        ) {
            Icon(
                imageVector = icon,
                tint = color,
            )
        }
    }
}

@Composable
fun ShareButton(
    status: UiStatus,
    compat: Boolean = false,
) {
    val context = AmbientContext.current
    val action = {
        context.shareText(status.rawText)
    }
    val icon = vectorResource(id = R.drawable.ic_share)
    if (compat) {
        TextButton(
            onClick = {
                action.invoke()
            },
            colors = ButtonDefaults.textButtonColors(
                contentColor = mediumEmphasisContentContentColor
            )
        ) {
            Icon(
                modifier = Modifier.size(statusActionIconSize),
                imageVector = icon,
            )
        }
    } else {
        ActionIconButton(
            onClick = {
                action.invoke()
            },
        ) {
            Icon(
                imageVector = icon,
                tint = mediumEmphasisContentContentColor,
            )
        }
    }
}

@Composable
private fun StatusActionButtonWithNumbers(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    count: Long,
    color: Color = AmbientContentColor.current,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier.weight(1f),
        horizontalArrangement = Arrangement.Start,
    ) {
        TextButton(
            onClick = onClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = color
            )
        ) {
            Icon(
                modifier = Modifier.size(statusActionIconSize),
                imageVector = icon,
                tint = color,
            )
            if (count > 0) {
                Box(modifier = Modifier.width(4.dp))
                Text(text = count.humanizedCount(), maxLines = 1)
            }
        }
    }
}
