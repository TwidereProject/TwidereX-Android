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
package com.twidere.services.nitter.model

import com.twidere.services.microblog.model.IStatus
import com.twidere.services.nitter.model.serializer.DateSerializer
import com.twidere.services.nitter.model.serializer.StatusBodySerializer
import com.twidere.services.nitter.model.serializer.StatusIdSerializer
import moe.tlaster.hson.annotations.HtmlSerializable
import java.util.Date

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
    @HtmlSerializable(".tweet-body .card")
    val card: StatusCard? = null,
) : IStatus
