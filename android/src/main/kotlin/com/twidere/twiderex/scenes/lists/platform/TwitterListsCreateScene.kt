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
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.lists.TwitterListsModifyComponent
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.navigation.RootRoute
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.lists.ListsCreateViewModel
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.PopUpTo

@Composable
fun TwitterListsCreateScene() {
    val account = LocalActiveAccount.current ?: return
    val navController = LocalNavController.current
    val listsCreateViewModel = assistedViewModel<ListsCreateViewModel.AssistedFactory, ListsCreateViewModel>(
        account
    ) {
        it.create(account) { success, list ->
            if (success) list?.apply {
                navController.navigate(
                    RootRoute.Lists.Timeline(listKey),
                    options = NavOptions(
                        popUpTo = PopUpTo(RootRoute.Lists.Home)
                    )
                )
            }
        }
    }
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
                        Text(text = stringResource(id = com.twidere.common.R.string.scene_lists_modify_create_title))
                    },
                    actions = {
                        IconButton(
                            enabled = name.isNotEmpty(),
                            onClick = {
                                listsCreateViewModel.createList(
                                    title = name,
                                    description = desc,
                                    private = isPrivate
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = stringResource(id = com.twidere.common.R.string.common_controls_actions_confirm),
                                tint = if (name.isNotEmpty()) MaterialTheme.colors.primary else LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
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
