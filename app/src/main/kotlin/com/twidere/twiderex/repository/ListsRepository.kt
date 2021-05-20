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
import com.twidere.services.microblog.ListsService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbList
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.ListsMode
import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.model.ui.UiList.Companion.toUi
import com.twidere.twiderex.paging.mediator.list.ListsMediator
import com.twidere.twiderex.paging.mediator.list.ListsMediator.Companion.toUi
import kotlinx.coroutines.flow.Flow

class ListsRepository(private val database: CacheDatabase) {

    fun findListWithListKey(account: AccountDetails, listKey: MicroBlogKey): LiveData<UiList?> {
        return database.listsDao().findWithListKeyWithLiveData(listKey = listKey, accountKey = account.accountKey)
            .map {
                it?.toUi()
            }
    }

    fun fetchLists(account: AccountDetails): Flow<PagingData<UiList>> {
        val mediator = ListsMediator(
            database = database,
            accountKey = account.accountKey,
            service = account.service as ListsService,
        )
        return mediator.pager().toUi()
    }

    suspend fun createLists(
        account: AccountDetails,
        title: String,
        description: String? = null,
        mode: String? = null,
        replyPolicy: String? = null
    ): UiList {
        val result = (account.service as ListsService).createList(
            name = title,
            description = description,
            mode = mode,
            repliesPolicy = replyPolicy
        ).toDbList(account.accountKey)
        // save to db
        database.listsDao().insertAll(listOf(result))
        return result.toUi()
    }

    suspend fun updateLists(
        account: AccountDetails,
        listId: String,
        title: String? = null,
        description: String? = null,
        mode: String? = null,
        replyPolicy: String? = null
    ): UiList {
        val result = (account.service as ListsService).updateList(
            listId = listId,
            name = title,
            description = description,
            mode = mode,
            repliesPolicy = replyPolicy
        ).toDbList(account.accountKey)
        val originSource = database.listsDao().findWithListKey(result.listKey, result.accountKey)
        originSource?.let {
            database.listsDao().update(
                listOf(
                    it.copy(
                        title = result.title,
                        description = result.description,
                        mode = result.mode,
                        isFollowed = result.isFollowed,
                        replyPolicy = result.replyPolicy,
                        allowToSubscribe = it.allowToSubscribe && result.mode != ListsMode.PRIVATE.value
                    )
                )
            )
        }
        return result.toUi()
    }

    suspend fun deleteLists(
        account: AccountDetails,
        listKey: MicroBlogKey,
        listId: String
    ): UiList? {
        (account.service as ListsService).destroyList(listId)
        val deleteItem = database.listsDao().findWithListKey(listKey, account.accountKey)
        deleteItem?.let {
            database.listsDao().delete(listOf(deleteItem))
        }
        return deleteItem?.toUi()
    }

    suspend fun unsubscribeLists(
        account: AccountDetails,
        listKey: MicroBlogKey
    ): UiList? {
        (account.service as ListsService).unsubscribeList(listId = listKey.id)
        val updateItem = database.listsDao().findWithListKey(listKey, account.accountKey)
        updateItem?.let {
            database.listsDao().update(listOf(updateItem.copy(isFollowed = false)))
        }
        return updateItem?.toUi()
    }

    suspend fun subscribeLists(
        account: AccountDetails,
        listKey: MicroBlogKey
    ): UiList {
        val result = (account.service as ListsService)
            .subscribeList(listId = listKey.id)
            .toDbList(accountKey = account.accountKey)
        val updateItem = database.listsDao().findWithListKey(listKey, account.accountKey)
        updateItem?.let {
            database.listsDao().update(listOf(updateItem.copy(isFollowed = true)))
        } ?: database.listsDao().insertAll(listOf(result))
        return result.toUi()
    }
}
