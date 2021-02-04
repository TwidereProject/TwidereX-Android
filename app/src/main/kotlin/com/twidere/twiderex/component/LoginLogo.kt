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
package com.twidere.twiderex.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.twidere.twiderex.R
import com.twidere.twiderex.extensions.isDarkTheme

@Composable
fun LoginLogo(
    modifier: Modifier = Modifier,
) {
    val resource = if (isDarkTheme()) {
        vectorResource(id = R.drawable.ic_login_logo_dark)
    } else {
        vectorResource(id = R.drawable.ic_login_logo)
    }
    Image(
        modifier = modifier,
        contentScale = ContentScale.FillWidth,
        imageVector = resource,
        contentDescription = stringResource(id = R.string.accessibility_common_logo_twidere)
    )
}
