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
package com.twidere.twiderex.extensions

import android.location.Location
import android.location.LocationManager

fun LocationManager.getCachedLocation(): Location? {
    var location: Location? = null
    try {
        location = getLastKnownLocation(LocationManager.GPS_PROVIDER)
    } catch (ignore: SecurityException) {
    }

    if (location != null) return location
    try {
        location = getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    } catch (ignore: SecurityException) {
    }

    return location
}
