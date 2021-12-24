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
package com.twidere.twiderex.mock.db.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.db.dao.DirectMessageEventDao
import com.twidere.twiderex.mock.paging.MockPagingSource
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMEvent
import org.jetbrains.annotations.TestOnly

internal class MockDirectMessageEventDao @TestOnly constructor() : DirectMessageEventDao {
    private val fakeDb = mutableMapOf<MicroBlogKey, MutableList<UiDMEvent>>()

    fun getLatestMessage(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): UiDMEvent? {
        return fakeDb[accountKey]?.filter { it.conversationKey == conversationKey }?.maxByOrNull { it.sortId }
    }

    override fun getPagingSource(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): PagingSource<Int, UiDMEvent> {
        return MockPagingSource(
            data = fakeDb[accountKey]?.filter { it.conversationKey == conversationKey } ?: emptyList()
        )
    }

    override suspend fun findWithMessageKey(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey,
        messageKey: MicroBlogKey
    ): UiDMEvent? {
        return fakeDb[accountKey]?.find { it.conversationKey == conversationKey && it.messageKey == messageKey }
    }

    override suspend fun delete(message: UiDMEvent) {
        fakeDb[message.accountKey]?.removeAll { it.messageKey == message.messageKey }
    }

    override suspend fun getMessageCount(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): Long {
        return fakeDb[accountKey]?.sumOf { if (it.conversationKey == conversationKey) 1L else 0L } ?: 0
    }

    override suspend fun insertAll(events: List<UiDMEvent>) {
        events.forEach { event ->
            fakeDb[event.accountKey].let {
                if (it.isNullOrEmpty()) {
                    fakeDb[event.accountKey] = mutableListOf(event)
                } else {
                    it.add(event)
                }
            }
        }
    }
}
