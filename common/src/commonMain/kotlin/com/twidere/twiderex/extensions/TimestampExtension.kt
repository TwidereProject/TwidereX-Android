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
package com.twidere.twiderex.extensions

import com.twidere.twiderex.kmp.TimeUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.humanizedTimestamp(): String {
    return TimeUtils.humanizedTimestamp(this)
}

fun Long.formattedTimestamp(): String {
    return SimpleDateFormat.getDateTimeInstance().format(Date(this))
}

val countUnits = arrayOf(null, "K", "M", "B")

fun Long.humanizedCount(): String {
    if (this < 1000) {
        return this.toString()
    }
    var value = this.toDouble()
    var index = 0
    while (index < countUnits.size) {
        if (value < 1000) {
            break
        }
        value /= 1000.0
        index++
    }
    return if (value < 10 && value % 1.0 >= 0.049 && value % 1.0 < 0.5) {
        String.format(Locale.getDefault(), "%.1f %s", value, countUnits[index])
    } else {
        String.format(Locale.getDefault(), "%.0f %s", value, countUnits[index])
    }
}
