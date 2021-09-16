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
package com.twidere.twiderex.kmp

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

actual object TimeUtils {
    private const val hour = 60
    private const val day = 24 * 60
    private const val week = 7 * 24 * 60
    actual fun humanizedTimestamp(time: Long): String {
        val now: Instant = Instant.now()
        val instant: Instant = Instant.ofEpochMilli(time)
        val relativeMinute = ChronoUnit.MINUTES.between(instant, now)
        return when {
            relativeMinute < hour -> "$relativeMinute min.ago"
            relativeMinute in (hour + 1) until day -> "${ChronoUnit.HOURS.between(instant, now)} hr.ago"
            relativeMinute in (day + 1) until week -> "${ChronoUnit.DAYS.between(instant, now)} days ago"
            else -> SimpleDateFormat.getDateTimeInstance().format(Date(time))
        }
    }
}
