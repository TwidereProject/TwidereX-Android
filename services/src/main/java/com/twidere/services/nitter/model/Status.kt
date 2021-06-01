package com.twidere.services.nitter.model

import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.IUser
import moe.tlaster.hson.annotations.HtmlSerializable
import moe.tlaster.hson.annotations.HtmlSerializer
import org.jsoup.nodes.Element
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
    val content: String,
    @HtmlSerializable(".replying-to")
    val replyTo: String?,
    @HtmlSerializable(".tweet-header")
    val user: User,
    @HtmlSerializable(".tweet-date > a", attr = "title", serializer = DateSerializer::class)
    val createdAt: Date,
    @HtmlSerializable(
        ".tweet-link",
        "quote-link",
        attr = "href",
        serializer = StatusIdSerializer::class
    )
    val statusId: String,
    @HtmlSerializable(".attachment")
    val attachments: List<Attachments>,
    @HtmlSerializable(".tweet-stats")
    val stats: StatusStats? = null,
    // @HtmlSerializable(".quote")
    // val quote: Status? = null,
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
    @HtmlSerializable(".tweet-avatar > img", ".fullname-and-username > .avatar", attr = "href")
    val avatar: String,
    @HtmlSerializable(".fullname-and-username > .fullname")
    val name: String,
    @HtmlSerializable(".fullname-and-username > .username")
    val screenName: String,
) : IUser

data class Attachments(
    @HtmlSerializable("img", attr = "src")
    val source: String?,
    @HtmlSerializable("video", attr = "poster")
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
        return "\\d+".toRegex().matchEntire(wholeText)?.value ?: ""
    }
}

class StatsSerializer : HtmlSerializer<Int> {
    override fun decode(element: Element, wholeText: String): Int {
        return element.parent().wholeText().trim().replace(",", "").toInt()
    }
}

class StatusBodySerializer: HtmlSerializer<String> {
    override fun decode(element: Element, wholeText: String): String {
        return element.html()
    }
}