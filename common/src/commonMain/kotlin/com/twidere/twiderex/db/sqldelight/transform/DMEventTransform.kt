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
package com.twidere.twiderex.db.sqldelight.transform

import com.twidere.twiderex.db.sqldelight.model.DbDMEventWithAttachments
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.sqldelight.table.DbDMEvent
import java.util.UUID

internal fun UiDMEvent.toDbEventWithAttachments(dbId: String = UUID.randomUUID().toString()) = DbDMEventWithAttachments(
    event = DbDMEvent(
        id = dbId,
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
        sendStatus = sendStatus
    ),
    media = media.map { it.toDbMedia() },
    url = urlEntity.map { it.toDbUrlEntity(messageKey) },
    sender = sender.toDbUser()
)

internal fun DbDMEventWithAttachments.toUi() = UiDMEvent(
    accountKey = event.accountKey,
    sortId = event.sortId,
    conversationKey = event.conversationKey,
    messageId = event.messageId,
    messageKey = event.messageKey,
    htmlText = event.htmlText,
    originText = event.originText,
    createdTimestamp = event.createdTimestamp,
    messageType = event.messageType,
    senderAccountKey = event.senderAccountKey,
    recipientAccountKey = event.recipientAccountKey,
    sendStatus = event.sendStatus,
    media = media.map { it.toUi() },
    urlEntity = url.map { it.toUi() },
    sender = sender.toUi()
)
