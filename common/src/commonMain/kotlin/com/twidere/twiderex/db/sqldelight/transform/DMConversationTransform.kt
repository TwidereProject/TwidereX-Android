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

import com.twidere.twiderex.db.sqldelight.model.DbDMConversationWithEvent
import com.twidere.twiderex.model.ui.UiDMConversation
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage
import com.twidere.twiderex.sqldelight.table.DbDMConversation

internal fun UiDMConversation.toDbDMConversation() = DbDMConversation(
    accountKey = accountKey,
    conversationKey = conversationKey,
    recipientKey = recipientKey,
    conversationId = conversationId,
    conversationAvatar = conversationAvatar,
    conversationName = conversationName,
    conversationSubName = conversationSubName,
    conversationType = conversationType
)

internal fun DbDMConversation.toUi() = UiDMConversation(
    accountKey = accountKey,
    conversationKey = conversationKey,
    recipientKey = recipientKey,
    conversationId = conversationId,
    conversationAvatar = conversationAvatar,
    conversationName = conversationName,
    conversationSubName = conversationSubName,
    conversationType = conversationType
)

internal fun DbDMConversationWithEvent.toUi() = UiDMConversationWithLatestMessage(
    conversation = conversation.toUi(),
    latestMessage = event.toUi()
)
