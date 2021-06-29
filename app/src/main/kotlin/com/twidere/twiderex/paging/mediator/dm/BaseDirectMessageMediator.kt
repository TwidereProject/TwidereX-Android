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
package com.twidere.twiderex.paging.mediator.dm

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.twidere.services.microblog.DirectMessageService
import com.twidere.services.microblog.model.IPaging
import com.twidere.services.twitter.model.DirectMessageEvent
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbDirectMessage
import com.twidere.twiderex.db.model.DbDMConversation
import com.twidere.twiderex.db.model.DbDMConversation.Companion.saveToDb
import com.twidere.twiderex.db.model.DbDMEventWithAttachments.Companion.saveToDb
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.model.MicroBlogKey
import java.util.UUID

@OptIn(ExperimentalPagingApi::class)
abstract class BaseDirectMessageMediator<Key : Any, Value : Any>(
    protected val database: CacheDatabase,
    protected val service: DirectMessageService,
    protected val accountKey: MicroBlogKey,
    protected val userLookup: suspend (userKey: MicroBlogKey) -> DbUser
) : RemoteMediator<Key, Value>() {
    private var paging: String? = null
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Key, Value>
    ): MediatorResult {
        return try {
            val key = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.APPEND -> if (reverse()) return MediatorResult.Success(endOfPaginationReached = true) else paging
                LoadType.PREPEND -> if (reverse()) paging else return MediatorResult.Success(endOfPaginationReached = true)
            }
            val result = service.getDirectMessages(key, 50)
            val events = result.map {
                if (it is DirectMessageEvent) {
                    it.toDbDirectMessage(accountKey, userLookup.invoke(MicroBlogKey.twitter(it.messageCreate?.senderId ?: "")))
                } else throw NotImplementedError()
            }
            // save message, media
            database.withTransaction {
                events.saveToDb(database)
                events.groupBy { it.message.conversationKey }
                    .map { entry ->
                        val msgWithData = entry.value.first()
                        val chatUser = msgWithData.message.conversationUserKey.let {
                            userLookup(it)
                        }
                        DbDMConversation(
                            _id = UUID.randomUUID().toString(),
                            accountKey = accountKey,
                            conversationId = msgWithData.message.conversationKey.id,
                            conversationKey = msgWithData.message.conversationKey,
                            conversationAvatar = chatUser.profileImage,
                            conversationName = chatUser.name,
                            conversationSubName = chatUser.screenName,
                            conversationType = DbDMConversation.Type.ONE_TO_ONE,
                            recipientKey = msgWithData.message.conversationUserKey
                        )
                    }.saveToDb(database)
            }
            paging = if (result is IPaging) {
                result.nextPage
            } else {
                null
            }
            MediatorResult.Success(endOfPaginationReached = paging == null)
        } catch (e: Throwable) {
            MediatorResult.Error(e)
        }
    }

    abstract fun reverse(): Boolean
}
