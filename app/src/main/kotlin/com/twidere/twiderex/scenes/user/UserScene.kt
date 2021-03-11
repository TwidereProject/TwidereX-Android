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
package com.twidere.twiderex.scenes.user

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.UserComponent
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.user.UserViewModel

@Composable
fun UserScene(
    userKey: MicroBlogKey,
) {
    val account = LocalActiveAccount.current ?: return
    val viewModel = assistedViewModel<UserViewModel.AssistedFactory, UserViewModel>(
        account,
        userKey,
    ) {
        it.create(account, userKey)
    }
    val user by viewModel.user.observeAsState(initial = null)
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
            UserComponent(userKey)
        }
    }
}
