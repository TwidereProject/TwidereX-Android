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
package com.twidere.twiderex.kmp

import androidx.compose.runtime.Composable
import com.twidere.twiderex.model.kmp.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
actual fun RequestLocationPermission(
    onPermissionGrantt: () -> Unit,
    content: @Composable (launchRequest: () -> Unit) -> Unit,
) {
    content.invoke {}
}

// TODO: implementation
actual class LocationProvider {
    actual val location: Flow<Location?>
        get() = flowOf(null)

    actual fun enable() {
    }

    actual fun disable() {
    }

    actual val locationEnabled: Flow<Boolean>
        get() = flowOf(false)
}
