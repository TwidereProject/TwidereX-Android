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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.twidere.twiderex.component.foundation.Dialog
import com.twidere.twiderex.component.foundation.LoadingProgress
import com.twidere.twiderex.component.lists.MastodonListsModifyComponent
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.viewmodel.lists.ListsCreateViewModel
import kotlinx.coroutines.launch

@Composable
fun MastodonListsCreateDialog(onDismissRequest: () -> Unit) {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
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
    val listsCreateViewModel: ListsCreateViewModel = getViewModel()
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
            title = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_modify_dialog_create),
            name = name,
            onNameChanged = { name = it }
        ) {
            scope.launch {
                val result = listsCreateViewModel.createList(
                    title = it
                )
                dismiss()
                if (result != null) {
                    navController.navigate(
                        Root.Lists.Timeline(result.listKey),
                    )
                }
            }
        }
    }
}
