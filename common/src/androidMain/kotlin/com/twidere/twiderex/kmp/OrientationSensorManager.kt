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

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.PI

actual class OrientationSensorManager(
    private val context: Context,
) : SensorEventListener {
    private val sensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val currentOrientationValues = FloatArray(3)
    private var originValues = floatArrayOf(0f, 0f, 0f)
    private var hasOriginInit = false

    init {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(
                    event.values,
                    0,
                    accelerometerReading,
                    0,
                    accelerometerReading.size
                )
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
            }
            else -> return
        }
        updateOrientationAngles()
    }

    private fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        val rotationOk = SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        // "mRotationMatrix" now has up-to-date information.
        if (rotationOk) {
            SensorManager.getOrientation(rotationMatrix, currentOrientationValues)
            // "mOrientationAngles" now has up-to-date information.
            if (!hasOriginInit) {
                originValues[0] = toDegree(currentOrientationValues[0])
                originValues[1] = toDegree(currentOrientationValues[1])
                originValues[2] = toDegree(currentOrientationValues[2])
                hasOriginInit = true
            }
            dispatchOrientationChanged()
        }
    }

    private fun toDegree(origin: Float): Float {
        return origin / PI.toFloat() * 180
    }

    private fun dispatchOrientationChanged() {
        // change current to degree
        onOrientationChangedListener?.invoke(
            originValues,
            currentOrientationValues.apply {
                this[0] = toDegree(this[0])
                this[1] = toDegree(this[1])
                this[2] = toDegree(this[2])
            }
        )
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // do nothing
    }

    actual fun release() {
        sensorManager.unregisterListener(this)
    }

    actual var onOrientationChangedListener: ((originValues: FloatArray, currentValues: FloatArray) -> Unit)? = null
}
