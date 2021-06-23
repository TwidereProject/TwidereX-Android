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
package com.twidere.twiderex.mock.service

import com.twidere.services.microblog.DirectMessageService
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.microblog.model.IDirectMessage
import com.twidere.services.twitter.model.Attachment
import com.twidere.services.twitter.model.DirectMessageEvent
import com.twidere.services.twitter.model.Entities
import com.twidere.services.twitter.model.EntitiesURL
import com.twidere.services.twitter.model.MessageCreate
import com.twidere.services.twitter.model.MessageData
import com.twidere.services.twitter.model.MessageTarget
import com.twidere.services.twitter.model.PurpleMedia
import kotlinx.coroutines.delay
import java.util.UUID

class MockDirectMessageService : DirectMessageService, MicroBlogService {
    private suspend fun generateDirectMessage(count: Int, senderId: String, recipientId: String): List<IDirectMessage> {
        val messageList = mutableListOf<IDirectMessage>()
        for (i in 0 until count) {
            messageList.add(
                DirectMessageEvent(
                    createdTimestamp = System.currentTimeMillis().toString(),
                    id = UUID.randomUUID().toString(),
                    type = "message_create",
                    messageCreate = MessageCreate(
                        messageData = MessageData(
                            text = "message:$count",
                            entities = Entities(
                                urls = listOf(
                                    EntitiesURL(
                                        display_url = "url$count",
                                        expanded_url = "expanded:$count",
                                        url = "url:$count",
                                        indices = listOf(0, 1)
                                    )
                                )
                            ),
                            attachment = Attachment(
                                type = "media",
                                media = PurpleMedia(
                                    id = count.toLong(),
                                    idStr = count.toString(),
                                )
                            )
                        ),
                        senderId = senderId,
                        target = MessageTarget(
                            recipientId
                        )
                    )
                )
            )
            delay(1)
        }
        return messageList
    }

    override suspend fun getDirectMessages(cursor: String?, count: Int?): List<IDirectMessage> {
        return generateDirectMessage(count ?: 50, senderId = System.currentTimeMillis().toString(), recipientId = UUID.randomUUID().toString())
    }

    override suspend fun showDirectMessage(id: String): IDirectMessage? {
        TODO("Not yet implemented")
    }

    override suspend fun destroyDirectMessage(id: String) {
        TODO("Not yet implemented")
    }
}
