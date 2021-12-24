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
package com.twidere.twiderex.room.db.transform

import com.twidere.twiderex.model.ui.UiDMConversation
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.room.db.model.DbDMConversation
import com.twidere.twiderex.room.db.model.DbDMEvent
import com.twidere.twiderex.room.db.model.DbDMEventWithAttachments
import com.twidere.twiderex.room.db.model.DbDirectMessageConversationWithMessage
import java.util.UUID

internal fun DbDMConversation.toUi() = UiDMConversation(
    accountKey = accountKey,
    conversationId = conversationId,
    conversationKey = conversationKey,
    conversationAvatar = conversationAvatar,
    conversationName = conversationName,
    conversationSubName = conversationSubName,
    conversationType = when (conversationType) {
        DbDMConversation.Type.ONE_TO_ONE -> UiDMConversation.Type.ONE_TO_ONE
        DbDMConversation.Type.GROUP -> UiDMConversation.Type.GROUP
    },
    recipientKey = recipientKey,
)

internal fun DbDirectMessageConversationWithMessage.toUi() = UiDMConversationWithLatestMessage(
    conversation = conversation.toUi(),
    latestMessage = latestMessage.toUi()
)

internal fun DbDMEventWithAttachments.toUi() = UiDMEvent(
    accountKey = message.accountKey,
    sortId = message.sortId,
    conversationKey = message.conversationKey,
    messageId = message.messageId,
    messageKey = message.messageKey,
    htmlText = message.htmlText,
    originText = message.originText,
    createdTimestamp = message.createdTimestamp,
    messageType = message.messageType,
    senderAccountKey = message.senderAccountKey,
    recipientAccountKey = message.recipientAccountKey,
    sendStatus = when (message.sendStatus) {
        DbDMEvent.SendStatus.PENDING -> UiDMEvent.SendStatus.PENDING
        DbDMEvent.SendStatus.SUCCESS -> UiDMEvent.SendStatus.SUCCESS
        DbDMEvent.SendStatus.FAILED -> UiDMEvent.SendStatus.FAILED
    },
    media = media.toUi(),
    urlEntity = urlEntity.toUi(),
    sender = sender.toUi()
)

internal fun UiDMConversation.toDbDMConversation(dbId: String = UUID.randomUUID().toString()) = DbDMConversation(
    accountKey = accountKey,
    conversationId = conversationId,
    conversationKey = conversationKey,
    conversationAvatar = conversationAvatar,
    conversationName = conversationName,
    conversationSubName = conversationSubName,
    conversationType = when (conversationType) {
        UiDMConversation.Type.ONE_TO_ONE -> DbDMConversation.Type.ONE_TO_ONE
        UiDMConversation.Type.GROUP -> DbDMConversation.Type.GROUP
    },
    recipientKey = recipientKey,
    _id = dbId
)

internal fun UiDMEvent.toDbMEventWithAttachments(
    dbId: String = UUID.randomUUID().toString(),
    dbSenderId: String = UUID.randomUUID().toString(),
) = DbDMEventWithAttachments(
    message = DbDMEvent(
        _id = dbId,
        accountKey = accountKey,
        sortId = sortId,
        conversationKey = conversationKey,
        messageId = messageId,
        messageKey = messageKey,
        htmlText = htmlText,
        originText = originText,
        createdTimestamp = createdTimestamp,
        messageType = messageType,
        senderAccountKey = senderAccountKey,
        recipientAccountKey = recipientAccountKey,
        sendStatus = when (sendStatus) {
            UiDMEvent.SendStatus.PENDING -> DbDMEvent.SendStatus.PENDING
            UiDMEvent.SendStatus.SUCCESS -> DbDMEvent.SendStatus.SUCCESS
            UiDMEvent.SendStatus.FAILED -> DbDMEvent.SendStatus.FAILED
        },
    ),
    media = media.toDbMedia(),
    urlEntity = urlEntity.toDbUrl(belongToKey = messageKey),
    sender = sender.toDbUser(dbId = dbSenderId)
)
