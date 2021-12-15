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
package com.twidere.twiderex.model.ui

import com.twidere.twiderex.model.MicroBlogKey

data class UiDMEvent(
    val accountKey: MicroBlogKey,
    val sortId: Long,
    // message
    val conversationKey: MicroBlogKey,
    val messageId: String,
    val messageKey: MicroBlogKey,
    // include hash tag in this parameter
    val htmlText: String,
    val originText: String,
    val createdTimestamp: Long,
    val messageType: String,
    val senderAccountKey: MicroBlogKey,
    val recipientAccountKey: MicroBlogKey,
    val sendStatus: SendStatus,
    val media: List<UiMedia>,
    val urlEntity: List<UiUrlEntity>,
    val sender: UiUser
) {
    val isInCome: Boolean
        get() = recipientAccountKey == accountKey

    val conversationUserKey: MicroBlogKey
        get() = if (accountKey == senderAccountKey) recipientAccountKey else senderAccountKey

    enum class SendStatus {
        PENDING,
        SUCCESS,
        FAILED
    }
}
