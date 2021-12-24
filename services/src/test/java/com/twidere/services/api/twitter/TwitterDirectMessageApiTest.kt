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
package com.twidere.services.api.twitter

import com.twidere.services.api.common.mockRetrofit
import com.twidere.services.twitter.api.DirectMessagesResources
import com.twidere.services.twitter.model.DirectMessageEventObject
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TwitterDirectMessageApiTest {
    private lateinit var dmResources: DirectMessagesResources

    @BeforeAll
    fun setUp() {
        dmResources = mockRetrofit("https://api.twitter.com/", TwitterRequest2AssetPathConvertor())
    }

    @Test
    fun createMessage() = runBlocking {
        val result = dmResources.sendMessage(DirectMessageEventObject())
        assertEquals("1406873583490502662", result.event?.id)
    }

    @Test
    fun getMessageList() = runBlocking {
        val result = dmResources.getMessages()
        assert(result.events?.isNotEmpty() ?: false)
    }

    @Test
    fun showMessage() = runBlocking {
        val result = dmResources.showMessage("1406873583490502662")
        assertEquals("1406873583490502662", result.event?.id)
    }

    @Test
    fun deleteMessage() = runBlocking {
        dmResources.destroyMessage("1406873583490502662")
        assert(true)
    }
}
