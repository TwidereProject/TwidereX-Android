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
package com.twidere.twiderex.model.ui

import com.twidere.twiderex.db.model.DbDMConversation
import com.twidere.twiderex.db.model.DbDirectMessageConversationWithMessage
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMConversation.Companion.toUi
import com.twidere.twiderex.model.ui.UiDMEvent.Companion.toUi

data class UiDMConversation(
    val accountKey: MicroBlogKey,
    // conversation
    val conversationId: String,
    val conversationKey: MicroBlogKey,
    val conversationAvatar: String,
    val conversationName: String,
    val conversationSubName: String,
    val conversationType: DbDMConversation.Type,
) {
    companion object {
        fun DbDMConversation.toUi() = UiDMConversation(
            accountKey = accountKey,
            conversationId = conversationId,
            conversationKey = conversationKey,
            conversationAvatar = conversationAvatar,
            conversationName = conversationName,
            conversationSubName = conversationSubName,
            conversationType = conversationType,
        )
    }
}

data class UiDMConversationWithLatestMessage(
    val conversation: UiDMConversation,
    val latestMessage: UiDMEvent
) {
    companion object {
        fun DbDirectMessageConversationWithMessage.toUi() = UiDMConversationWithLatestMessage(
            conversation = conversation.toUi(),
            latestMessage = latestMessage.toUi()
        )
    }
}
