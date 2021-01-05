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
package com.twidere.twiderex.scenes

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.UserComponent
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.ui.TwidereXTheme

@OptIn(IncomingComposeUpdate::class)
@Composable
fun UserScene(
    screenName: String,
    host: String,
    userKey: MicroBlogKey?,
) {
    TwidereXTheme {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    backgroundColor = MaterialTheme.colors.surface.withElevation(),
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(imageVector = vectorResource(id = R.drawable.ic_mail))
                        }
                        IconButton(onClick = {}) {
                            Icon(imageVector = vectorResource(id = R.drawable.ic_dots_vertical))
                        }
                    },
                    elevation = 0.dp,
                )
            }
        ) {
            UserComponent(screenName, host, userKey)
        }
    }
}
