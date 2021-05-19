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
package com.twidere.twiderex.scenes.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.twidere.twiderex.R
import com.twidere.twiderex.component.UserComponent
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalIsActiveEdgeToEdge

class MeItem : HomeNavigationItem() {

    @Composable
    override fun name(): String = stringResource(R.string.scene_profile_title)

    @Composable
    override fun icon(): Painter = painterResource(id = R.drawable.ic_user)

    override val withAppBar: Boolean
        get() = false

    @Composable
    override fun content() {
        val account = LocalActiveAccount.current
        account?.toUi()?.let { user ->
            InAppNotificationScaffold {
                val tabTop = with(LocalDensity.current) { LocalWindowInsets.current.statusBars.top.toDp() }
                UserComponent(
                    userKey = user.userKey,
                    tabTopPadding = if (LocalIsActiveEdgeToEdge.current)
                        PaddingValues(top = tabTop)
                    else
                        PaddingValues(0.dp)
                )
            }
        }
    }
}
