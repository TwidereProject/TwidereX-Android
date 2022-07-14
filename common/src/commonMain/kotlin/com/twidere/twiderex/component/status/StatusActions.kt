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
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.action.LocalStatusActions
import com.twidere.twiderex.component.foundation.DropdownMenu
import com.twidere.twiderex.component.foundation.DropdownMenuItem
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.extensions.humanizedCount
import com.twidere.twiderex.kmp.LocalRemoteNavigator
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.LocalActiveAccount

private val rippleSize = 24.dp

@Composable
fun ReplyButton(
    modifier: Modifier = Modifier,
    status: UiStatus,
    withNumber: Boolean = true,
) {
    val icon = painterResource(res = com.twidere.twiderex.MR.files.ic_corner_up_left)
    val contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_common_status_actions_reply)

    val navigator = LocalNavigator.current
    val action = {
        navigator.compose(ComposeType.Reply, statusKey = status.statusKey)
    }

    if (withNumber) {
        val data = status.retweet ?: status
        StatusActionButtonWithNumbers(
            modifier = modifier,
            icon = icon,
            color = LocalContentColor.current,
            count = data.metrics.reply,
            contentDescription = contentDescription,
            onClick = {
                action.invoke()
            },
        )
    } else {
        IconButton(
            modifier = modifier,
            onClick = {
                action.invoke()
            },
        ) {
            Icon(
                painter = icon,
                tint = LocalContentColor.current,
                contentDescription = contentDescription,
            )
        }
    }
}

@Composable
fun LikeButton(
    modifier: Modifier = Modifier,
    status: UiStatus,
    withNumber: Boolean = true,
) {
    val actionsViewModel = LocalStatusActions.current
    val account = LocalActiveAccount.current
    val color = if (status.liked) {
        Color.Red
    } else {
        LocalContentColor.current
    }
    val contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_common_status_actions_like)
    val icon = painterResource(res = com.twidere.twiderex.MR.files.ic_heart)
    val action = {
        if (account != null) {
            actionsViewModel.like(status, account)
        }
    }
    val data = status.retweet ?: status
    if (withNumber) {
        StatusActionButtonWithNumbers(
            modifier = modifier,
            icon = icon,
            count = data.metrics.like,
            color = color,
            contentDescription = contentDescription,
            onClick = {
                action.invoke()
            },
        )
    } else {
        IconButton(
            modifier = modifier,
            onClick = {
                action.invoke()
            },
        ) {
            Icon(
                painter = icon,
                tint = color,
                contentDescription = contentDescription,
            )
        }
    }
}

@Composable
fun RetweetButton(
    modifier: Modifier = Modifier,
    status: UiStatus,
    withNumber: Boolean = true,
) {
    val actionsViewModel = LocalStatusActions.current
    val account = LocalActiveAccount.current
    val color = if (status.retweeted) {
        MaterialTheme.colors.primary
    } else {
        LocalContentColor.current
    }
    val icon = painterResource(res = com.twidere.twiderex.MR.files.ic_repeat)
    val contentDescription =
        stringResource(res = com.twidere.twiderex.MR.strings.accessibility_common_status_actions_retweet)
    var expanded by remember { mutableStateOf(false) }
    val retweetAction = {
        if (status.platformType == PlatformType.Twitter) {
            expanded = true
        } else {
            if (account != null) {
                actionsViewModel.retweet(status = status, account = account)
            }
        }
    }
    Box(
        modifier = modifier,
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                onClick = {
                    if (account != null) {
                        actionsViewModel.retweet(status, account)
                    }
                    expanded = false
                }
            ) {
                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_status_actions_retweet))
            }
            val navigator = LocalNavigator.current
            DropdownMenuItem(
                onClick = {
                    navigator.compose(ComposeType.Quote, statusKey = status.statusKey)
                }
            ) {
                Text(
                    text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_status_actions_quote),
                )
            }
        }
        val data = status.retweet ?: status
        if (withNumber) {
            StatusActionButtonWithNumbers(
                icon = icon,
                count = data.metrics.retweet,
                color = color,
                contentDescription = contentDescription,
                onClick = {
                    retweetAction.invoke()
                },
            )
        } else {
            IconButton(
                onClick = {
                    retweetAction.invoke()
                },
            ) {
                Icon(
                    painter = icon,
                    tint = color,
                    contentDescription = contentDescription,
                )
            }
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun ShareButton(
    modifier: Modifier = Modifier,
    status: UiStatus,
    compat: Boolean = false,
    menus: @Composable ColumnScope.(callback: () -> Unit) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    val data = status.retweet ?: status
    val actionsViewModel = LocalStatusActions.current
    val account = LocalActiveAccount.current
    val accountKey = account?.accountKey
    val remoteNavigator = LocalRemoteNavigator.current
    val icon = Icons.Default.MoreHoriz
    val text = renderContentAnnotatedString(
        htmlText = data.htmlText,
        linkResolver = { data.resolveLink(it) },
    )
    val clipboardManager = LocalClipboardManager.current
    val contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_common_more)
    Box(
        modifier = modifier,
    ) {
        if (compat) {
            Box(
                modifier = Modifier
                    .defaultMinSize(
                        minHeight = ButtonDefaults.MinHeight
                    )
                    .clickable(
                        indication = rememberRipple(
                            bounded = false,
                            radius = rippleSize
                        ),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            expanded = true
                        },
                    )
                    .padding(StatusActionsDefaults.ContentPadding),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = LocalContentColor.current.copy(LocalContentAlpha.current)
                )
            }
        } else {
            IconButton(
                onClick = {
                    expanded = true
                },
            ) {
                Icon(
                    imageVector = icon,
                    tint = LocalContentColor.current,
                    contentDescription = contentDescription,
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            menus.invoke(this) {
                expanded = false
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    clipboardManager.setText(text)
                }
            ) {
                Text(
                    text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_status_actions_copy_text),
                )
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    clipboardManager.setText(
                        buildAnnotatedString {
                            append(status.generateShareLink())
                        }
                    )
                }
            ) {
                Text(
                    text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_status_actions_copy_link),
                )
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    remoteNavigator.shareText(
                        status.generateShareLink()
                    )
                }
            ) {
                Text(
                    text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_status_actions_share_link),
                )
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    remoteNavigator.shareText(
                        buildString {
                            append(text)
                            append(System.lineSeparator())
                            append(System.lineSeparator())
                            append(status.generateShareLink())
                        }
                    )
                }
            ) {
                Text(
                    text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_status_actions_share_content),
                )
            }
            if (data.user.userKey == accountKey) {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        actionsViewModel.delete(data, account)
                    }
                ) {
                    Text(
                        text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_remove),
                        color = Color.Red,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusActionButtonWithNumbers(
    modifier: Modifier = Modifier,
    icon: Painter,
    contentDescription: String,
    count: Long,
    enabled: Boolean = true,
    color: Color = LocalContentColor.current,
    onClick: () -> Unit,
) {
    val contentColor = color.copy(LocalContentAlpha.current)
    val source = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .defaultMinSize(
                minHeight = ButtonDefaults.MinHeight
            )
            .clickable(
                onClick = onClick,
                enabled = enabled,
                interactionSource = source,
                indication = null,
            )
            .padding(StatusActionsDefaults.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .size(MaterialTheme.typography.body1.fontSize.value.dp)
                .indication(
                    source,
                    rememberRipple(
                        bounded = false,
                        radius = rippleSize
                    )
                ),
            tint = contentColor,
            painter = icon,
            contentDescription = contentDescription,
        )
        if (count > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = count.humanizedCount(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body2,
                color = contentColor,
            )
        }
    }
}

object StatusActionsDefaults {
    val ContentPadding = 8.dp
}
