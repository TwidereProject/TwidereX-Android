package com.twidere.twiderex.component.status

import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope.Companion.weight
import androidx.compose.foundation.layout.width
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.ButtonConstants
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Reply
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.twidere.twiderex.component.foundation.ActionIconButton
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.providers.AmbientStatusActions
import com.twidere.twiderex.scenes.ComposeType
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.AmbientInStoryboard
import com.twidere.twiderex.ui.AmbientNavController

@Composable
fun ReplyButton(
    status: UiStatus,
    withNumber: Boolean = true,
) {
    val inStoryBoard = AmbientInStoryboard.current
    val navController = AmbientNavController.current
    val icon = Icons.Default.Reply
    val action = {
        if (!inStoryBoard) {
            navController.navigate("compose/${ComposeType.Reply.name}?statusId=${status.statusId}")
        }
    }
    if (withNumber) {
        StatusActionButtonWithNumbers(
            icon = icon,
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
            Icon(asset = icon)
        }
    }
}

@Composable
fun LikeButton(
    status: UiStatus,
    withNumber: Boolean = true,
) {
    val actionsViewModel = AmbientStatusActions.current
    val inStoryBoard = AmbientInStoryboard.current
    val account = AmbientActiveAccount.current
    val color = if (status.liked) {
        Color.Red
    } else {
        AmbientContentColor.current
    }
    val icon = Icons.Default.Favorite
    val action = {
        if (!inStoryBoard && account != null) {
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
                asset = Icons.Default.Favorite,
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
    val inStoryBoard = AmbientInStoryboard.current
    val account = AmbientActiveAccount.current
    val color = if (status.retweeted) {
        MaterialTheme.colors.primary
    } else {
        AmbientContentColor.current
    }
    val icon = Icons.Default.Comment
    val action = {
        if (!inStoryBoard && account != null) {
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
                asset = icon,
                tint = color,
            )
        }
    }
}


@Composable
private fun StatusActionButtonWithNumbers(
    modifier: Modifier = Modifier.weight(1f),
    icon: VectorAsset,
    count: Long,
    color: Color = AmbientContentColor.current,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
    ) {
        TextButton(
            onClick = onClick,
            colors = ButtonConstants.defaultTextButtonColors(
                contentColor = AmbientContentColor.current
            )
        ) {
            ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                Icon(
                    asset = icon,
                    tint = color
                )
                if (count > 0) {
                    Box(modifier = Modifier.width(4.dp))
                    Text(text = count.toString())
                }
            }
        }
    }
}
