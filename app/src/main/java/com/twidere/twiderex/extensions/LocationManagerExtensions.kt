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
