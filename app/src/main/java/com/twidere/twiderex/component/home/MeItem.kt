/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.component.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.vector.VectorAsset
import com.twidere.twiderex.extensions.navViewModel
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.UserComponent
import com.twidere.twiderex.viewmodel.MeViewModel

class MeItem : HomeNavigationItem() {
    override val name: String
        get() = "Me"
    override val icon: VectorAsset
        get() = Icons.Default.AccountCircle
    override val withAppBar: Boolean
        get() = false

    @OptIn(IncomingComposeUpdate::class)
    @Composable
    override fun onCompose() {
        val viewModel = navViewModel<MeViewModel>()
        val user by viewModel.user.observeAsState()
        user?.let {
            UserComponent(data = it)
        }
    }
}
