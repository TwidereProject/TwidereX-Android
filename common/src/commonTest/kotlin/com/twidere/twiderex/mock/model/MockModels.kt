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
import com.twidere.services.twitter.model.Attachment
import com.twidere.services.twitter.model.AttachmentsV2
import com.twidere.services.twitter.model.DirectMessageEvent
import com.twidere.services.twitter.model.Entities
import com.twidere.services.twitter.model.EntitiesURL
import com.twidere.services.twitter.model.MediaV2
import com.twidere.services.twitter.model.MessageCreate
import com.twidere.services.twitter.model.MessageData
import com.twidere.services.twitter.model.MessageTarget
import com.twidere.services.twitter.model.PurpleMedia
import com.twidere.services.twitter.model.ReferencedTweetType
import com.twidere.services.twitter.model.ReferencedTweetV2
import com.twidere.services.twitter.model.StatusV2
import com.twidere.services.twitter.model.TwitterList
import com.twidere.services.twitter.model.TwitterPaging
import com.twidere.services.twitter.model.UserV2
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.ui.UiDMConversation
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.model.ui.UiDraft
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiSearch
import com.twidere.twiderex.model.ui.UiUrlEntity
import org.jetbrains.annotations.TestOnly
import java.util.Date
import java.util.UUID

@TestOnly
fun mockUiMedia(url: String = "", belongToKey: MicroBlogKey = MicroBlogKey.Empty, order: Int = 0) = UiMedia(
    url = url,
    belongToKey = belongToKey,
    mediaUrl = url,
    previewUrl = url,
    type = MediaType.photo,
    width = 100,
    height = 100,
    pageUrl = "",
    altText = "",
    order = order
)

@TestOnly
fun mockUiDraft(
    draftId: String = UUID.randomUUID().toString(),
    content: String = "",
    composeType: ComposeType = ComposeType.New,
    statusKey: MicroBlogKey = MicroBlogKey.twitter(UUID.randomUUID().toString())
) = UiDraft(
    draftId = draftId,
    content = content,
    media = emptyList(),
    createdAt = System.currentTimeMillis(),
    composeType = composeType,
    statusKey = statusKey,
    excludedReplyUserIds = null
)

@TestOnly
fun mockUiSearch(content: String = "", accountKey: MicroBlogKey = MicroBlogKey.Empty, saved: Boolean = false) = UiSearch(
    content = content,
    lastActive = System.currentTimeMillis(),
    saved = saved,
    accountKey = accountKey
)

fun <T> List<T>.toIPaging(nextPaging: String? = UUID.randomUUID().toString()) = TwitterPaging(
    data = this,
    nextPage = nextPaging
)

@TestOnly
fun mockIUser(id: String = UUID.randomUUID().toString(), name: String = ""): IUser {
    return UserV2(
        id = id,
        name = name
    )
}

@TestOnly
fun mockITrend(name: String = "trend timestamp:${System.currentTimeMillis()}"): ITrend {
    return Trend(
        name = name,
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
fun mockIListModel(
    name: String = "",
    mode: String? = null,
    description: String? = "",
): IListModel {
    val id = UUID.randomUUID().hashCode().toLong()
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
fun mockIStatus(
    id: String = UUID.randomUUID().toString(),
    hasMedia: Boolean = false,
    authorId: String = UUID.randomUUID().toString(),
    hasReference: Boolean = false,
    text: String = "text"
): IStatus {
    return StatusV2(
        id = id,
        authorID = authorId,
        createdAt = Date().apply { time = System.currentTimeMillis() },
        attachments = if (hasMedia) AttachmentsV2(mediaKeys = listOf("mediaKey")).apply {
            media = listOf(MediaV2(url = "mediaUrl", type = "photo"))
        } else null,

        referencedTweets = if (hasReference) listOf(
            ReferencedTweetV2(
                type = ReferencedTweetType.retweeted,
                id = UUID.randomUUID().toString()
            ).apply { status = mockIStatus() as StatusV2 }
        ) else emptyList(),
        text = text
    ).apply {
        user = UserV2(
            id = authorId,
        )
    }
}

@TestOnly
fun mockINotification(id: String = UUID.randomUUID().toString()): INotification {
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
fun mockIDirectMessage(id: String = UUID.randomUUID().toString(), accountId: String, otherUserID: String = UUID.randomUUID().toString(), inCome: Boolean = true): IDirectMessage {
    return DirectMessageEvent(
        createdTimestamp = System.currentTimeMillis().toString(),
        id = id,
        type = "message_create",
        messageCreate = MessageCreate(
            messageData = MessageData(
                text = "mock message",
                entities = Entities(
                    urls = listOf(
                        EntitiesURL(
                            display_url = "http://test.com",
                            url = "http://test.com",
                            expanded_url = "http://test.com",
                        )
                    )
                ),
                attachment = Attachment(
                    type = "media",
                    media = PurpleMedia()
                )
            ),
            senderId = if (inCome) otherUserID else accountId,
            target = MessageTarget(
                recipientId = if (inCome) accountId else otherUserID
            )
        )
    )
}

@TestOnly
fun mockUiUrlEntity(url: String = "") = UiUrlEntity(
    url = url,
    displayUrl = "displayUrl",
    expandedUrl = "expandedUrl",
    title = "title",
    description = "description",
    image = "image"
)

@TestOnly
fun UiDMEvent.toConversation() = UiDMConversation(
    accountKey = accountKey,
    conversationId = conversationKey.id,
    conversationKey = conversationKey,
    conversationAvatar = sender.profileImage.toString(),
    conversationName = sender.name,
    conversationSubName = sender.screenName,
    conversationType = UiDMConversation.Type.ONE_TO_ONE,
    recipientKey = conversationUserKey
)
