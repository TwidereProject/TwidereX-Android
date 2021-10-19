package com.twidere.twiderex.kmp

actual class OrientationSensorManager {
    actual var onOrientationChangedListener: ((originValues: FloatArray, currentValues: FloatArray) -> Unit)? = null
}