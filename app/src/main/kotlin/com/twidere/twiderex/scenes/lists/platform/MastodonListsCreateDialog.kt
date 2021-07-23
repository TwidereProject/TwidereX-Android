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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.lists.MastodonListsModifyComponent
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.navigation.RootRoute
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.viewmodel.lists.ListsCreateViewModel

@Composable
fun MastodonListsCreateDialog(onDismissRequest: () -> Unit) {
    val account = LocalActiveAccount.current ?: return
    val navController = LocalNavController.current
    var showMastodonComponent by remember {
        mutableStateOf(true)
    }
    val dismiss = {
        onDismissRequest.invoke()
        showMastodonComponent = true
    }
    var name by remember {
        mutableStateOf("")
    }
    val listsCreateViewModel = assistedViewModel<ListsCreateViewModel.AssistedFactory, ListsCreateViewModel>(
        account
    ) {
        it.create(account) { success, list ->
            dismiss()
            if (success) {
                list?.apply {
                    navController.navigate(
                        RootRoute.Lists.Timeline(listKey),
                    )
                }
            }
        }
    }
    val loading by listsCreateViewModel.loading.observeAsState(initial = false)

    if (loading) {
        Dialog(
            onDismissRequest = {
                dismiss()
            }
        ) {
            LoadingProgress()
        }
        return
    }

    if (showMastodonComponent) {
        MastodonListsModifyComponent(
            onDismissRequest = { dismiss() },
            title = stringResource(id = R.string.scene_lists_modify_dialog_create),
            name = name,
            onNameChanged = { name = it }
        ) {
            listsCreateViewModel.createList(
                title = it
            )
        }
    }
}
