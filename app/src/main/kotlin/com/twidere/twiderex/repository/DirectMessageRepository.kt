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
import com.twidere.services.microblog.DirectMessageService
import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.db.model.DbUser
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiDMConversation
import com.twidere.twiderex.model.ui.UiDMConversation.Companion.toUi
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.paging.mediator.dm.DMConversationMediator
import com.twidere.twiderex.paging.mediator.dm.DMConversationMediator.Companion.toUi
import com.twidere.twiderex.paging.mediator.dm.DMEventMediator
import com.twidere.twiderex.paging.mediator.dm.DMEventMediator.Companion.toUi
import kotlinx.coroutines.flow.Flow

class DirectMessageRepository(
    private val database: CacheDatabase
) {
    fun dmConversation(
        account: AccountDetails,
        conversationKey: MicroBlogKey
    ): LiveData<UiDMConversation?> {
        return database.directMessageConversationDao()
            .findWithConversationKey(
                accountKey = account.accountKey,
                conversationKey = conversationKey
            ).map { it?.toUi() }
    }

    fun dmConversationListSource(
        account: AccountDetails,
    ): Flow<PagingData<UiDMConversationWithLatestMessage>> {
        return DMConversationMediator(
            database = database,
            service = account.service as DirectMessageService,
            accountKey = account.accountKey,
            userLookup = { userKey ->
                lookupUser(account, userKey)
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
            service = account.service as DirectMessageService,
            accountKey = account.accountKey,
            userLookup = { userKey ->
                lookupUser(account, userKey)
            }
        ).pager().toUi()
    }

    private suspend fun lookupUser(account: AccountDetails, userKey: MicroBlogKey): DbUser {
        return database.userDao().findWithUserKey(userKey) ?: let {
            val user = (account.service as LookupService).lookupUser(userKey.id)
                .toDbUser(account.accountKey)
            database.userDao().insertAll(listOf(user))
            user
        }
    }
}
