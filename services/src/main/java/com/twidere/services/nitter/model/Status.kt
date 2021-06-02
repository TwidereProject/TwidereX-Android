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
package com.twidere.services.nitter.model

import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.IUser
import moe.tlaster.hson.HtmlSerializer
import moe.tlaster.hson.annotations.HtmlSerializable
import org.jsoup.nodes.Element
import java.net.URLDecoder
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class UserTimelineStatus(
    @HtmlSerializable(".timeline-item:not(.show-more)")
    val statuses: List<Status>
)

data class Status(
    @HtmlSerializable(".media-body", ".quote-text", serializer = StatusBodySerializer::class)
    val content: String?,
    @HtmlSerializable(".replying-to")
    val replyTo: String?,
    @HtmlSerializable(".tweet-header", ".tweet-name-row")
    val user: User?,
    @HtmlSerializable(".tweet-date > a", attr = "title", serializer = DateSerializer::class)
    val createdAt: Date?,
    @HtmlSerializable(
        ".tweet-link",
        ".quote-link",
        attr = "href",
        serializer = StatusIdSerializer::class
    )
    val statusId: String?,
    @HtmlSerializable(".attachment")
    val attachments: List<Attachments>?,
    @HtmlSerializable(".tweet-stats")
    val stats: StatusStats? = null,
    @HtmlSerializable(".quote")
    val quote: Status? = null,
) : IStatus

data class StatusStats(
    @HtmlSerializable(".tweet-stat .icon-comment", serializer = StatsSerializer::class)
    val comment: Int,
    @HtmlSerializable(".tweet-stat .icon-retweet", serializer = StatsSerializer::class)
    val retweet: Int,
    @HtmlSerializable(".tweet-stat .icon-quote", serializer = StatsSerializer::class)
    val quote: Int,
    @HtmlSerializable(".tweet-stat .icon-heart", serializer = StatsSerializer::class)
    val like: Int,
)

data class User(
    @HtmlSerializable(
        ".tweet-avatar > img",
        ".fullname-and-username > .avatar",
        attr = "src",
        serializer = UrlDecodeSerializer::class,
    )
    val avatar: String?,
    @HtmlSerializable(".fullname-and-username > .fullname")
    val name: String?,
    @HtmlSerializable(".fullname-and-username > .username")
    val screenName: String?,
) : IUser

data class Attachments(
    @HtmlSerializable("img", attr = "src", serializer = UrlDecodeSerializer::class)
    val source: String?,
    @HtmlSerializable("video", attr = "poster", serializer = UrlDecodeSerializer::class)
    val videoCover: String?,
)

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

class StatusIdSerializer : HtmlSerializer<String> {
    override fun decode(element: Element, wholeText: String): String {
        return "status/(\\d+)".toRegex().find(wholeText)?.groupValues?.getOrNull(1) ?: ""
    }
}

class StatsSerializer : HtmlSerializer<Int> {
    override fun decode(element: Element, wholeText: String): Int {
        return element.parent().wholeText().trim().replace(",", "").toInt()
    }
}

class StatusBodySerializer : HtmlSerializer<String> {
    override fun decode(element: Element, wholeText: String): String {
        return element.html()
    }
}

class UrlDecodeSerializer : HtmlSerializer<String> {
    override fun decode(element: Element, wholeText: String): String {
        return URLDecoder.decode(wholeText, Charset.forName("UTF-8"))
    }
}
