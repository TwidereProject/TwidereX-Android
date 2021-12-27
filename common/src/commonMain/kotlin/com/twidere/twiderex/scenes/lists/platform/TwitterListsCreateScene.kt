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
package com.twidere.twiderex.scenes.lists.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.Dialog
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.lists.TwitterListsModifyComponent
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.lists.ListsCreateViewModel
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.PopUpTo

@Composable
fun TwitterListsCreateScene() {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val listsCreateViewModel: ListsCreateViewModel = getViewModel()
    val loading by listsCreateViewModel.loading.observeAsState(initial = false)

    TwidereScene {
        var name by remember {
            mutableStateOf("")
        }
        var desc by remember {
            mutableStateOf("")
        }
        var isPrivate by remember {
            mutableStateOf(false)
        }
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = { AppBarNavigationButton(Icons.Default.Close) },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_modify_create_title))
                    },
                    actions = {
                        IconButton(
                            enabled = name.isNotEmpty(),
                            onClick = {
                                scope.launch {
                                    listsCreateViewModel.createList(
                                        title = name,
                                        description = desc,
                                        private = isPrivate
                                    )?.let {
                                        navController.navigate(
                                            Root.Lists.Timeline(it.listKey),
                                            options = NavOptions(
                                                popUpTo = PopUpTo(Root.Lists.Home)
                                            )
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_confirm),
                                tint = if (name.isNotEmpty()) MaterialTheme.colors.primary else LocalContentColor.current.copy(
                                    alpha = LocalContentAlpha.current
                                )
                            )
                        }
                    }
                )
            }
        ) {
            Box {
                TwitterListsModifyComponent(
                    name = name,
                    desc = desc,
                    isPrivate = isPrivate,
                    onNameChanged = { name = it },
                    onDescChanged = { desc = it },
                ) {
                    isPrivate = it
                }
                if (loading) {
                    Dialog(onDismissRequest = { }) {
                        LoadingProgress()
                    }
                }
            }
        }
    }
}
