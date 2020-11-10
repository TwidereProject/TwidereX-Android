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
package com.twidere.twiderex.scenes

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.UserComponent
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.ui.TwidereXTheme

@OptIn(IncomingComposeUpdate::class)
@Composable
fun UserScene(name: String) {
    TwidereXTheme {
        Scaffold(
            topBar = {
                AppBar(
                    backgroundColor = MaterialTheme.colors.surface.withElevation(),
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.Mail)
                        }
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.MoreVert)
                        }
                    },
                    elevation = 0.dp,
                )
            }
        ) {
            UserComponent(screenName = name)
        }
    }
}
