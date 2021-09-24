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
package com.twidere.twiderex.db.sqldelight.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.db.dao.DirectMessageEventDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMEvent

internal class SqlDelightDirectMessageEventDaoImpl : DirectMessageEventDao {
    override fun getPagingSource(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): PagingSource<Int, UiDMEvent> {
        TODO("Not yet implemented")
    }

    override suspend fun findWithMessageKey(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey,
        messageKey: MicroBlogKey
    ): UiDMEvent? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(message: UiDMEvent) {
        TODO("Not yet implemented")
    }

    override suspend fun getMessageCount(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): Long {
        TODO("Not yet implemented")
    }

    override suspend fun insertAll(events: List<UiDMEvent>) {
        TODO("Not yet implemented")
    }
}
