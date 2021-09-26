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
package com.twidere.twiderex.db.sqldelight.transform

import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.sqldelight.model.DbDMEventWithAttachments
import com.twidere.twiderex.mock.model.mockIUser
import com.twidere.twiderex.mock.model.mockUiMedia
import com.twidere.twiderex.mock.model.mockUiUrlEntity
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMEvent
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class DMEventTransformTest {
    @Test
    fun transform() {
        val ui = UiDMEvent(
            accountKey = MicroBlogKey.twitter("account"),
            sender = mockIUser().toUi(MicroBlogKey.twitter("sender")),
            sortId = System.currentTimeMillis(),
            conversationKey = MicroBlogKey.twitter("conversation"),
            messageId = System.currentTimeMillis().toString(),
            messageKey = MicroBlogKey.twitter("messageKey"),
            htmlText = "htmlText",
            originText = "originText",
            createdTimestamp = System.currentTimeMillis(),
            messageType = "create",
            senderAccountKey = MicroBlogKey.twitter("sender"),
            recipientAccountKey = MicroBlogKey.twitter("recipient"),
            sendStatus = UiDMEvent.SendStatus.SUCCESS,
            media = listOf(mockUiMedia()),
            urlEntity = listOf(mockUiUrlEntity())
        )
        val id = UUID.randomUUID().toString()
        val db = ui.toDbEventWithAttachments(dbId = id)
        assertSuccess(db, ui)

        val uiFromDb = db.toUi()
        assertSuccess(db, uiFromDb)
    }

    private fun assertSuccess(db: DbDMEventWithAttachments, ui: UiDMEvent) {
        assertEquals(db.event.accountKey, ui.accountKey)
        assertEquals(db.sender.userKey, ui.sender.userKey)
        assertEquals(db.event.sortId, ui.sortId)
        assertEquals(db.event.conversationKey, ui.conversationKey)
        assertEquals(db.event.messageId, ui.messageId)
        assertEquals(db.event.messageKey, ui.messageKey)
        assertEquals(db.event.htmlText, ui.htmlText)
        assertEquals(db.event.originText, ui.originText)
        assertEquals(db.event.createdTimestamp, ui.createdTimestamp)
        assertEquals(db.event.messageType, ui.messageType)
        assertEquals(db.event.senderAccountKey, ui.senderAccountKey)
        assertEquals(db.event.recipientAccountKey, ui.recipientAccountKey)
        assertEquals(db.event.sendStatus, ui.sendStatus)
        assertEquals(db.media.first().url, ui.media.first().url)
        assertEquals(db.url.first().url, ui.urlEntity.first().url)
    }
}
