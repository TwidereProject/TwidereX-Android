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
package com.twidere.twiderex.repository

import androidx.paging.PagingData
import com.twidere.services.http.MicroBlogNotFoundException
import com.twidere.services.microblog.DirectMessageService
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.model.IDirectMessage
import com.twidere.services.twitter.model.DirectMessageEvent
import com.twidere.services.twitter.model.exceptions.TwitterApiException
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.model.ui.UiDMConversation
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.paging.mediator.dm.DMConversationMediator
import com.twidere.twiderex.paging.mediator.dm.DMConversationMediator.Companion.toUi
import com.twidere.twiderex.paging.mediator.dm.DMEventMediator
import com.twidere.twiderex.paging.mediator.dm.DMEventMediator.Companion.toUi
import kotlinx.coroutines.flow.Flow

class DirectMessageRepository(
    private val database: CacheDatabase
) {
    private val userNotFound = mutableListOf<MicroBlogKey>()
    fun dmConversation(
        accountKey: MicroBlogKey,
        conversationKey: MicroBlogKey
    ): Flow<UiDMConversation?> {
        return database.directMessageConversationDao()
            .findWithConversationKeyFlow(
                accountKey = accountKey,
                conversationKey = conversationKey
            )
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
                            UiDMConversation(
                                accountKey = accountKey,
                                conversationId = conversationId,
                                conversationKey = conversationKey,
                                conversationAvatar = receiver.profileImage.toString(),
                                conversationName = receiver.displayName,
                                conversationSubName = receiver.screenName,
                                conversationType = UiDMConversation.Type.ONE_TO_ONE,
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
                        it.latestMessage.messageKey == con.latestMessage.messageKey
                    }?.let { true } ?: false
                needDrop
            }
            newConversation
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
            database.directMessageDao().delete(it)
            try {
                service.destroyDirectMessage(messageId)
            } catch (e: TwitterApiException) {
                // code 34 means this message not exists on server, ignore this error, continue delete it form db
                if (e.errors?.first()?.code != 34) throw e
            }
            // if conversation is empty, delete conversation too
            if (database.directMessageDao().getMessageCount(accountKey, conversationKey) == 0L) {
                val conversation =
                    database.directMessageConversationDao().findWithConversationKey(accountKey, conversationKey)
                conversation?.let {
                    database.directMessageConversationDao().delete(it)
                }
            }
        }
    }

    suspend fun fetchEventAndSaveToDataBase(key: String?, accountKey: MicroBlogKey, service: DirectMessageService, lookupService: LookupService): List<IDirectMessage> {
        val result = service.getDirectMessages(key, 50)
        val events = result.mapNotNull {
            if (it is DirectMessageEvent) {
                // check if sender and receiver is not null
                lookupUser(
                    accountKey,
                    MicroBlogKey.twitter(it.messageCreate?.senderId ?: ""),
                    lookupService
                )?.let { sender ->
                    it.toUi(accountKey, sender)
                }?.let { event ->
                    if (lookupUser(accountKey, event.recipientAccountKey, lookupService) != null) event else null
                }
            } else null
        }
        // save message, media
        database.withTransaction {
            database.directMessageDao().insertAll(events)
            events.groupBy { it.conversationKey }
                .mapNotNull { entry ->
                    val msgWithData = entry.value.first()
                    lookupUser(accountKey, msgWithData.conversationUserKey, lookupService)?.let {
                        UiDMConversation(
                            accountKey = accountKey,
                            conversationId = msgWithData.conversationKey.id,
                            conversationKey = msgWithData.conversationKey,
                            conversationAvatar = it.profileImage.toString(),
                            conversationName = it.name,
                            conversationSubName = it.screenName,
                            conversationType = UiDMConversation.Type.ONE_TO_ONE,
                            recipientKey = msgWithData.conversationUserKey
                        )
                    }
                }.let {
                    database.directMessageConversationDao().insertAll(it)
                }
        }
        return result
    }

    private suspend fun lookupUser(accountKey: MicroBlogKey, userKey: MicroBlogKey, service: LookupService): UiUser? {
        return database.userDao().findWithUserKey(userKey) ?: let {
            try {
                if (userNotFound.contains(userKey)) return null
                val user = service.lookupUser(userKey.id)
                    .toUi(accountKey)
                database.userDao().insertAll(listOf(user))
                user
            } catch (e: MicroBlogNotFoundException) {
                userNotFound.add(userKey)
                null
            }
        }
    }
}
