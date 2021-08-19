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
package com.twidere.twiderex.dataprovider.db.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.db.dao.DirectMessageConversationDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMConversation
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage
import com.twidere.twiderex.room.db.dao.RoomDirectMessageConversationDao
import com.twidere.twiderex.room.db.transform.toDbDMConversation
import com.twidere.twiderex.room.db.transform.toUi
import kotlinx.coroutines.flow.map

internal class DirectMessageConversationDaoImpl(private val roomConversationDao: RoomDirectMessageConversationDao) : DirectMessageConversationDao {
    override fun getPagingSource(accountKey: MicroBlogKey): PagingSource<Int, UiDMConversationWithLatestMessage> {
        TODO("Not yet implemented")
    }

    override fun findWithConversationKeyFlow(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ) = roomConversationDao.findWithConversationKeyFlow(accountKey, conversationKey).map { it?.toUi() }

    override suspend fun findWithConversationKey(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ) = roomConversationDao.findWithConversationKey(accountKey, conversationKey)?.toUi()

    override suspend fun insertAll(listOf: List<UiDMConversation>) {
        roomConversationDao.insertAll(listOf.map { it.toDbDMConversation() })
    }

    override suspend fun find(
        accountKey: MicroBlogKey
    ) = roomConversationDao.find(accountKey).map { it.toUi() }

    override suspend fun delete(conversation: UiDMConversation) {
        roomConversationDao.delete(conversation.toDbDMConversation())
    }
}
