package com.twidere.twiderex.kmp

expect class OrientationSensorManager {
    var onOrientationChangedListener: ((originValues: FloatArray, currentValues: FloatArray) -> Unit)?
}