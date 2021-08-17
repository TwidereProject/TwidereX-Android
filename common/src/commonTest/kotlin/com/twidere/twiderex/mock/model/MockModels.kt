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
package com.twidere.twiderex.mock.model

import com.twidere.services.mastodon.model.Account
import com.twidere.services.mastodon.model.Notification
import com.twidere.services.mastodon.model.NotificationTypes
import com.twidere.services.mastodon.model.Status
import com.twidere.services.mastodon.model.Trend
import com.twidere.services.mastodon.model.TrendHistory
import com.twidere.services.microblog.model.IDirectMessage
import com.twidere.services.microblog.model.IListModel
import com.twidere.services.microblog.model.INotification
import com.twidere.services.microblog.model.IStatus
import com.twidere.services.microblog.model.ITrend
import com.twidere.services.microblog.model.IUser
import com.twidere.services.twitter.model.AttachmentsV2
import com.twidere.services.twitter.model.DirectMessageEvent
import com.twidere.services.twitter.model.MediaV2
import com.twidere.services.twitter.model.MessageCreate
import com.twidere.services.twitter.model.MessageData
import com.twidere.services.twitter.model.MessageTarget
import com.twidere.services.twitter.model.StatusV2
import com.twidere.services.twitter.model.TwitterList
import com.twidere.services.twitter.model.TwitterPaging
import com.twidere.services.twitter.model.UserV2
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiSearch
import org.jetbrains.annotations.TestOnly
import java.util.Date
import java.util.UUID

@TestOnly
internal fun mockUiMedia(url: String = "", belongToKey: MicroBlogKey = MicroBlogKey.Empty) = UiMedia(
    url = url,
    belongToKey = belongToKey,
    mediaUrl = url,
    previewUrl = url,
    type = MediaType.photo,
    width = 100,
    height = 100,
    pageUrl = "",
    altText = "",
    order = 0
)

@TestOnly
internal fun mockUiSearch(content: String = "", accountKey: MicroBlogKey = MicroBlogKey.Empty, saved: Boolean = false) = UiSearch(
    content = content,
    lastActive = System.currentTimeMillis(),
    saved = saved,
    accountKey = accountKey
)

internal fun <T> List<T>.toIPaging(nextPaging: String? = UUID.randomUUID().toString()) = TwitterPaging(
    data = this,
    nextPage = nextPaging
)

@TestOnly
internal fun mockIUser(id: String = UUID.randomUUID().toString(), name: String = ""): IUser {
    return UserV2(
        id = id,
        name = name
    )
}

@TestOnly
internal fun mockITrend(): ITrend {
    return Trend(
        name = "trend timestamp:${System.currentTimeMillis()}",
        url = "https://trend",

        history = mutableListOf(
            TrendHistory(
                accounts = "1",
                uses = "1",
                day = System.currentTimeMillis().toString()
            )
        )
    )
}

@TestOnly
internal fun mockIListModel(
    name: String = "",
    mode: String? = null,
    description: String? = "",
): IListModel {
    val id = System.currentTimeMillis()
    return TwitterList(
        id = id,
        idStr = id.toString(),
        name = name,
        mode = mode,
        description = description,
        createdAt = Date().apply { time = System.currentTimeMillis() }
    )
}

@TestOnly
internal fun mockIStatus(
    id: String = UUID.randomUUID().toString(),
    hasMedia: Boolean = false,
    authorId: String = UUID.randomUUID().toString()
): IStatus {
    return StatusV2(
        id = id,
        authorID = authorId,
        createdAt = Date().apply { time = System.currentTimeMillis() },
        attachments = if (hasMedia) AttachmentsV2(mediaKeys = listOf("mediaKey")).apply {
            media = listOf(MediaV2(url = "mediaUrl"))
        } else null
    ).apply {
        user = UserV2(
            id = authorId,
        )
    }
}

@TestOnly
internal fun mockINotification(id: String = UUID.randomUUID().toString()): INotification {
    val account = Account(
        id = UUID.randomUUID().toString(),
        username = "",
        displayName = "",
        acct = ""
    )
    return Notification(
        id = id,
        type = NotificationTypes.status,
        createdAt = Date().apply { time = System.currentTimeMillis() },
        account = account,
        status = Status(
            id = id,
            account = account
        )
    )
}

@TestOnly
internal fun mockIDirectMessage(id: String = UUID.randomUUID().toString(), accountId: String, otherUserID: String = UUID.randomUUID().toString(), inCome: Boolean = true): IDirectMessage {
    return DirectMessageEvent(
        createdTimestamp = System.currentTimeMillis().toString(),
        id = id,
        type = "message_create",
        messageCreate = MessageCreate(
            messageData = MessageData(text = "mock message"),
            senderId = if (inCome) otherUserID else accountId,
            target = MessageTarget(
                recipientId = if (inCome) accountId else otherUserID
            )
        )
    )
}