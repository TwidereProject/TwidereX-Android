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
package com.twidere.twiderex.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.twidere.services.microblog.DirectMessageService
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.model.IDirectMessage
import com.twidere.services.twitter.model.DirectMessageEvent
import com.twidere.services.twitter.model.exceptions.TwitterApiException
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbDirectMessage
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.DbDMConversation
import com.twidere.twiderex.db.model.DbDMConversation.Companion.saveToDb
import com.twidere.twiderex.db.model.DbDMEventWithAttachments.Companion.saveToDb
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiDMConversation
import com.twidere.twiderex.model.ui.UiDMConversation.Companion.toUi
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage.Companion.toUi
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.paging.mediator.dm.DMConversationMediator
import com.twidere.twiderex.paging.mediator.dm.DMConversationMediator.Companion.toUi
import com.twidere.twiderex.paging.mediator.dm.DMEventMediator
import com.twidere.twiderex.paging.mediator.dm.DMEventMediator.Companion.toUi
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class DirectMessageRepository(
    private val database: CacheDatabase
) {
    fun dmConversation(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): LiveData<UiDMConversation?> {
        return database.directMessageConversationDao()
            .findWithConversationKeyLiveData(
                accountKey = accountKey,
                conversationKey = conversationKey
            ).map { it?.toUi() }
    }

    fun dmConversationListSource(
        accountKey: MicroBlogKey,
        service: DirectMessageService,
        lookupService: LookupService
    ): Flow<PagingData<UiDMConversationWithLatestMessage>> {
        return DMConversationMediator(
            database = database,
            accountKey = accountKey,
            realFetch = { key ->
                fetchEventAndSaveToDataBase(key, accountKey, service, lookupService)
            }
        ).pager().toUi()
    }

    fun dmEventListSource(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey,
        service: DirectMessageService,
        lookupService: LookupService,
    ): Flow<PagingData<UiDMEvent>> {
        return DMEventMediator(
            database = database,
            conversationKey = conversationKey,
            accountKey = accountKey,
            realFetch = { key ->
                fetchEventAndSaveToDataBase(key, accountKey, service, lookupService)
            }
        ).pager().toUi()
    }

    suspend fun createNewConversation(
        receiver: UiUser,
        accountKey: MicroBlogKey,
        platformType: PlatformType
    ): MicroBlogKey {
        if (platformType != PlatformType.Twitter) throw Error()
        val conversationId = "${accountKey.id}-${receiver.id}"
        val conversationKey = MicroBlogKey.twitter(conversationId)

        return database.withTransaction {
            database.directMessageConversationDao()
                .findWithConversationKey(accountKey, conversationKey)
                ?.conversationKey
                ?: let {
                    database.directMessageConversationDao().insertAll(
                        listOf(
                            DbDMConversation(
                                _id = UUID.randomUUID().toString(),
                                accountKey = accountKey,
                                conversationId = conversationId,
                                conversationKey = conversationKey,
                                conversationAvatar = receiver.profileImage.toString(),
                                conversationName = receiver.displayName,
                                conversationSubName = receiver.screenName,
                                conversationType = DbDMConversation.Type.ONE_TO_ONE,
                                recipientKey = receiver.userKey
                            )
                        )
                    )
                    conversationKey
                }
        }
    }

    suspend fun checkNewMessages(accountKey: MicroBlogKey, service: DirectMessageService, lookupService: LookupService): List<UiDMConversationWithLatestMessage> {
        return database.withTransaction {
            val oldConversation = database.directMessageConversationDao().find(accountKey)
            fetchEventAndSaveToDataBase(null, accountKey = accountKey, service = service, lookupService = lookupService)
            val newConversation = database.directMessageConversationDao().find(accountKey).toMutableList()
            newConversation.removeAll { con ->
                val needDrop = con.latestMessage.sender.userKey == accountKey ||
                    oldConversation.find {
                    // self send message or same received message
                    it.latestMessage.message.messageKey == con.latestMessage.message.messageKey
                }?.let { true } ?: false
                needDrop
            }
            newConversation
        }.map {
            it.toUi()
        }
    }

    suspend fun deleteMessage(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey,
        messageId: String,
        messageKey: MicroBlogKey,
        service: DirectMessageService
    ) = database.withTransaction {
        database.directMessageDao().findWithMessageKey(
            accountKey,
            conversationKey,
            messageKey
        )?.let {
            database.directMessageDao().delete(it.message)
            try {
                service.destroyDirectMessage(messageId)
            } catch (e: TwitterApiException) {
                // code 34 means this message not exists on server, ignore this error, continue delete it form db
                if (e.errors?.first()?.code != 34) throw e
            }
            // if conversation is empty, delete conversation too
            if (database.directMessageDao().getMessageCount(accountKey, conversationKey) == 0L) {
                val conversation = database.directMessageConversationDao().findWithConversationKey(accountKey, conversationKey)
                conversation?.let {
                    database.directMessageConversationDao().delete(it)
                }
            }
        }
    }

    suspend fun fetchEventAndSaveToDataBase(key: String?, accountKey: MicroBlogKey, service: DirectMessageService, lookupService: LookupService): List<IDirectMessage> {
        val result = service.getDirectMessages(key, 50)
        val events = result.map {
            if (it is DirectMessageEvent) {
                it.toDbDirectMessage(accountKey, lookupUser(accountKey, MicroBlogKey.twitter(it.messageCreate?.senderId ?: ""), lookupService))
            } else throw NotImplementedError()
        }
        // save message, media
        database.withTransaction {
            events.saveToDb(database)
            events.groupBy { it.message.conversationKey }
                .map { entry ->
                    val msgWithData = entry.value.first()
                    val chatUser = msgWithData.message.conversationUserKey.let {
                        lookupUser(accountKey, it, lookupService)
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
        return result
    }

    private suspend fun lookupUser(accountKey: MicroBlogKey, userKey: MicroBlogKey, service: LookupService): DbUser {
        return database.userDao().findWithUserKey(userKey) ?: let {
            val user = service.lookupUser(userKey.id)
                .toDbUser(accountKey)
            database.userDao().insertAll(listOf(user))
            user
        }
    }
}
