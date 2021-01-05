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

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.component.UserListComponent
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.extensions.viewModel
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.viewmodel.twitter.user.FollowingViewModel

@Composable
fun TwitterFollowingScene(
    userKey: MicroBlogKey,
) {
    val account = AmbientActiveAccount.current ?: return
    val viewModel = viewModel(
        account,
        userKey,
    ) {
        FollowingViewModel(account, userKey)
    }
    TwidereXTheme {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(stringResource(id = R.string.scene_following_title))
                    }
                )
            },
        ) {
            UserListComponent(viewModel)
        }
    }
}
