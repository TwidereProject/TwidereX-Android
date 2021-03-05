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
package com.twidere.twiderex.scenes.twitter.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.popUpTo
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.viewmodel.twitter.user.TwitterUserViewModel

@Composable
fun TwitterUserScene(screenName: String) {
    val account = LocalActiveAccount.current ?: return
    val viewModel =
        assistedViewModel<TwitterUserViewModel.AssistedFactory, TwitterUserViewModel>(
            account,
            screenName
        ) {
            it.create(account, screenName)
        }
    val user by viewModel.user.observeAsState(initial = null)
    val error by viewModel.error.observeAsState(initial = null)
    val navigator = LocalNavigator.current
    LaunchedEffect(user) {
        user?.let {
            navigator.user(user = it) {
                popUpTo(Route.DeepLink.Twitter.User) {
                    inclusive = true
                }
            }
        }
    }

    TwidereXTheme {
        InAppNotificationScaffold {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                error?.let {
                    Icon(
                        Icons.Default.Error,
                        modifier = Modifier.size(40.dp),
                        contentDescription = null,
                    )
                } ?: run {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
