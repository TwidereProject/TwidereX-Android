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
package com.twidere.twiderex.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.twidere.services.mastodon.model.Visibility
import com.twidere.twiderex.R

@Composable
fun Visibility.icon(): Painter {
    return when (this) {
        Visibility.Public -> painterResource(id = R.drawable.ic_globe)
        Visibility.Unlisted -> painterResource(id = R.drawable.ic_lock_open)
        Visibility.Private -> painterResource(id = R.drawable.ic_lock)
        Visibility.Direct -> painterResource(id = R.drawable.ic_mail)
    }
}
