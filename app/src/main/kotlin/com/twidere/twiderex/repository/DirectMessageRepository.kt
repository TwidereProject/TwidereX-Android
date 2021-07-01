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
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbDirectMessage
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.DbDMConversation
import com.twidere.twiderex.db.model.DbDMConversation.Companion.saveToDb
import com.twidere.twiderex.db.model.DbDMEventWithAttachments.Companion.saveToDb
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.model.AccountDetails
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
        account: AccountDetails,
        conversationKey: MicroBlogKey
    ): LiveData<UiDMConversation?> {
        return database.directMessageConversationDao()
            .findWithConversationKeyLiveData(
                accountKey = account.accountKey,
                conversationKey = conversationKey
            ).map { it?.toUi() }
    }

    fun dmConversationListSource(
        account: AccountDetails,
    ): Flow<PagingData<UiDMConversationWithLatestMessage>> {
        return DMConversationMediator(
            database = database,
            accountKey = account.accountKey,
            realFetch = { key ->
                fetchEventAndSaveToDataBase(key, account)
            }
        ).pager().toUi()
    }

    fun dmEventListSource(
        account: AccountDetails,
        conversationKey: MicroBlogKey
    ): Flow<PagingData<UiDMEvent>> {
        return DMEventMediator(
            database = database,
            conversationKey = conversationKey,
            accountKey = account.accountKey,
            realFetch = { key ->
                fetchEventAndSaveToDataBase(key, account)
            }
        ).pager().toUi()
    }

    suspend fun createNewConversation(receiver: UiUser, account: AccountDetails): MicroBlogKey {
        val conversationId = "${account.accountKey.id}-${receiver.id}"
        val conversationKey = when (account.type) {
            PlatformType.Twitter -> MicroBlogKey.twitter(conversationId)
            PlatformType.StatusNet -> TODO()
            PlatformType.Fanfou -> TODO()
            PlatformType.Mastodon -> TODO()
        }
        return database.withTransaction {
            database.directMessageConversationDao()
                .findWithConversationKey(account.accountKey, conversationKey)
                ?.conversationKey
                ?: let {
                    database.directMessageConversationDao().insertAll(
                        listOf(
                            DbDMConversation(
                                _id = UUID.randomUUID().toString(),
                                accountKey = account.accountKey,
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

    suspend fun checkNewMessages(account: AccountDetails): List<UiDMConversationWithLatestMessage> {
        return database.withTransaction {
            val oldConversation = database.directMessageConversationDao().find(account.accountKey)
            fetchEventAndSaveToDataBase(null, account = account)
            val newConversation = database.directMessageConversationDao().find(account.accountKey)
            newConversation.dropWhile { con ->
                oldConversation.find {
                    // self send message or same received message
                    con.latestMessage.sender.userKey == account.accountKey ||
                        it.latestMessage.message.messageKey == con.latestMessage.message.messageKey
                }
                    ?.let { true } ?: false
            }
        }.map {
            it.toUi()
        }
    }

    private suspend fun lookupUser(account: AccountDetails, userKey: MicroBlogKey): DbUser {
        return database.userDao().findWithUserKey(userKey) ?: let {
            val user = (account.service as LookupService).lookupUser(userKey.id)
                .toDbUser(account.accountKey)
            database.userDao().insertAll(listOf(user))
            user
        }
    }

    private suspend fun fetchEventAndSaveToDataBase(key: String?, account: AccountDetails): List<IDirectMessage> {
        val service = account.service as DirectMessageService
        val accountKey = account.accountKey
        val result = service.getDirectMessages(key, 50)
        val events = result.map {
            if (it is DirectMessageEvent) {
                it.toDbDirectMessage(accountKey, lookupUser(account, MicroBlogKey.twitter(it.messageCreate?.senderId ?: "")))
            } else throw NotImplementedError()
        }
        // save message, media
        database.withTransaction {
            events.saveToDb(database)
            events.groupBy { it.message.conversationKey }
                .map { entry ->
                    val msgWithData = entry.value.first()
                    val chatUser = msgWithData.message.conversationUserKey.let {
                        lookupUser(account, it)
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
}
