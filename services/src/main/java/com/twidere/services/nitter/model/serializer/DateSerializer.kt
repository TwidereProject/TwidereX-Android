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
package com.twidere.services.nitter.model.serializer

import moe.tlaster.hson.HtmlSerializer
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateSerializer : HtmlSerializer<Date> {
    override fun decode(element: Element, wholeText: String): Date {
        return getDateFormat().parse(wholeText)
    }

    private fun getDateFormat(): SimpleDateFormat {
        val format = SimpleDateFormat("dd/MM/yyyy, HH:mm:ss", Locale.ENGLISH)
        format.isLenient = true
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format
    }
}
