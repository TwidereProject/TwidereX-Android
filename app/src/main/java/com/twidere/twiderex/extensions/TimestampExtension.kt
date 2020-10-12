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

import android.content.res.Resources
import android.os.Build
import org.ocpsoft.prettytime.PrettyTime
import java.text.DateFormat
import java.util.Date

private val prettyTime = PrettyTime(
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
        Resources.getSystem().configuration.locales[0]
    } else {
        Resources.getSystem().configuration.locale
    }
)

private val SECOND_MILLIS = 1000
private val MINUTE_MILLIS = 60 * SECOND_MILLIS
private val HOUR_MILLIS = 60 * MINUTE_MILLIS
private val DAY_MILLIS = 24 * HOUR_MILLIS

fun Long.humanizedTimestamp(): String {
    val date = Date(this)
    return if (System.currentTimeMillis() - date.time > 3 * HOUR_MILLIS) {
        DateFormat.getDateTimeInstance().format(date)
    } else {
        prettyTime.format(date)
    }
}
