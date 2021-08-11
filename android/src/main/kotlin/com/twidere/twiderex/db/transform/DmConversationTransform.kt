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
package com.twidere.twiderex.db.transform

import com.twidere.twiderex.db.model.DbDMConversation
import com.twidere.twiderex.db.model.DbDMEvent
import com.twidere.twiderex.db.model.DbDMEventWithAttachments
import com.twidere.twiderex.db.model.DbDirectMessageConversationWithMessage
import com.twidere.twiderex.model.ui.UiDMConversation
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage
import com.twidere.twiderex.model.ui.UiDMEvent

fun DbDMConversation.toUi() = UiDMConversation(
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

fun DbDirectMessageConversationWithMessage.toUi() = UiDMConversationWithLatestMessage(
    conversation = conversation.toUi(),
    latestMessage = latestMessage.toUi()
)

fun DbDMEventWithAttachments.toUi() = UiDMEvent(
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
