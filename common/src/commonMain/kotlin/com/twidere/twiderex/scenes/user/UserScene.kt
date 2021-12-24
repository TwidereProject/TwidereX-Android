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
package com.twidere.twiderex.scenes.user

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.MR
import com.twidere.twiderex.component.UserComponent
import com.twidere.twiderex.component.foundation.AlertDialog
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.DropdownMenu
import com.twidere.twiderex.component.foundation.DropdownMenuItem
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.dm.DMNewConversationViewModel
import com.twidere.twiderex.viewmodel.user.UserViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UserScene(
    userKey: MicroBlogKey,
) {
    val account = LocalActiveAccount.current ?: return
    val viewModel: UserViewModel = getViewModel {
        parametersOf(userKey)
    }
    val conversationViewModel: DMNewConversationViewModel = getViewModel()
    val user by viewModel.user.observeAsState(initial = null)
    val navController = LocalNavController.current
    var expanded by remember { mutableStateOf(false) }
    var showBlockAlert by remember { mutableStateOf(false) }
    val relationship by viewModel.relationship.observeAsState(initial = null)
    val loadingRelationship by viewModel.loadingRelationship.observeAsState(initial = false)
    TwidereScene {
        InAppNotificationScaffold(
            // TODO: Show top bar with actions
            topBar = {
                AppBar(
                    backgroundColor = MaterialTheme.colors.surface.withElevation(),
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    actions = {
                        if (account.type == PlatformType.Twitter && user?.platformType == PlatformType.Twitter) {
                            user?.let {
                                if (userKey != account.accountKey) {
                                    IconButton(
                                        onClick = {
                                            conversationViewModel.createNewConversation(
                                                it,
                                                onResult = { conversationKey ->
                                                    conversationKey?.let {
                                                        navController.navigate(Root.Messages.Conversation(it))
                                                    }
                                                }
                                            )
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(res = com.twidere.twiderex.MR.files.ic_mail),
                                            contentDescription = stringResource(
                                                res = com.twidere.twiderex.MR.strings.scene_messages_title
                                            ),
                                            tint = MaterialTheme.colors.onSurface
                                        )
                                    }
                                }
                            }
                        }
                        Box {
                            if (userKey != account.accountKey) {
                                IconButton(
                                    onClick = {
                                        expanded = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreHoriz,
                                        contentDescription = stringResource(
                                            res = MR.strings.accessibility_common_more
                                        ),
                                        tint = MaterialTheme.colors.onSurface
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                relationship.takeIf { !loadingRelationship }
                                    ?.blocking?.let { blocking ->
                                        DropdownMenuItem(
                                            onClick = {
                                                if (blocking)
                                                    viewModel.unblock()
                                                else
                                                    showBlockAlert = true
                                                expanded = false
                                            }
                                        ) {
                                            Text(
                                                text = stringResource(
                                                    res = if (blocking) MR.strings.common_controls_friendship_actions_unblock
                                                    else MR.strings.common_controls_friendship_actions_block
                                                )
                                            )
                                        }
                                    }
                            }
                        }
                    },
                    elevation = 0.dp,
                    title = {
                        user?.let {
                            UserName(user = it)
                        }
                    }
                )
            }
        ) {
            Box {
                UserComponent(userKey)
                if (showBlockAlert) {
                    user?.let {
                        BlockAlert(
                            screenName = it.getDisplayScreenName(it.userKey.host),
                            onDismissRequest = { showBlockAlert = false },
                            onConfirm = {
                                viewModel.block()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BlockAlert(
    screenName: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismissRequest.invoke()
        },
        title = {
            Text(
                text = stringResource(res = MR.strings.common_alerts_block_user_confirm_title, screenName),
                style = MaterialTheme.typography.subtitle1
            )
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest.invoke()
                }
            ) {
                Text(text = stringResource(res = MR.strings.common_controls_actions_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismissRequest.invoke()
                }
            ) {
                Text(text = stringResource(res = MR.strings.common_controls_actions_yes))
            }
        },
    )
}
