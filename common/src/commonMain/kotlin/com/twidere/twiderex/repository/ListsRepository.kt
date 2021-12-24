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
import com.twidere.services.microblog.ListsService
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.ListsMode
import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.paging.mediator.list.ListsMediator
import com.twidere.twiderex.paging.mediator.list.ListsMediator.Companion.toUi
import kotlinx.coroutines.flow.Flow

class ListsRepository(private val database: CacheDatabase) {

    fun findListWithListKey(accountKey: MicroBlogKey, listKey: MicroBlogKey): Flow<UiList?> {
        return database.listsDao().findWithListKeyWithFlow(listKey = listKey, accountKey = accountKey)
    }

    fun fetchLists(accountKey: MicroBlogKey, service: ListsService): Flow<PagingData<UiList>> {
        val mediator = ListsMediator(
            database = database,
            accountKey = accountKey,
            service = service,
        )
        return mediator.pager().toUi()
    }

    suspend fun createLists(
        accountKey: MicroBlogKey,
        service: ListsService,
        title: String,
        description: String? = null,
        mode: String? = null,
        replyPolicy: String? = null
    ): UiList {
        val result = service.createList(
            name = title,
            description = description,
            mode = mode,
            repliesPolicy = replyPolicy
        ).toUi(accountKey)
        // save to db
        database.listsDao().insertAll(listOf(result))
        return result
    }

    suspend fun updateLists(
        accountKey: MicroBlogKey,
        service: ListsService,
        listId: String,
        title: String? = null,
        description: String? = null,
        mode: String? = null,
        replyPolicy: String? = null
    ): UiList {
        val result = service.updateList(
            listId = listId,
            name = title,
            description = description,
            mode = mode,
            repliesPolicy = replyPolicy
        ).toUi(accountKey)
        val originSource = database.listsDao().findWithListKey(result.listKey, result.accountKey)
        originSource?.let {
            database.listsDao().update(
                listOf(
                    it.copy(
                        title = result.title,
                        descriptions = result.descriptions,
                        mode = result.mode,
                        isFollowed = result.isFollowed,
                        replyPolicy = result.replyPolicy,
                        allowToSubscribe = it.allowToSubscribe && result.mode != ListsMode.PRIVATE.value
                    )
                )
            )
        }
        return result
    }

    suspend fun deleteLists(
        accountKey: MicroBlogKey,
        service: ListsService,
        listKey: MicroBlogKey,
        listId: String
    ): UiList? {
        service.destroyList(listId)
        val deleteItem = database.listsDao().findWithListKey(listKey, accountKey)
        deleteItem?.let {
            database.listsDao().delete(listOf(deleteItem))
        }
        return deleteItem
    }

    suspend fun unsubscribeLists(
        accountKey: MicroBlogKey,
        service: ListsService,
        listKey: MicroBlogKey
    ): UiList? {
        service.unsubscribeList(listId = listKey.id)
        val updateItem = database.listsDao().findWithListKey(listKey, accountKey)
        updateItem?.let {
            database.listsDao().update(listOf(updateItem.copy(isFollowed = false)))
        }
        return updateItem
    }

    suspend fun subscribeLists(
        accountKey: MicroBlogKey,
        service: ListsService,
        listKey: MicroBlogKey
    ): UiList {
        val result = service
            .subscribeList(listId = listKey.id)
            .toUi(accountKey = accountKey)
        val updateItem = database.listsDao().findWithListKey(listKey, accountKey)
        updateItem?.let {
            database.listsDao().update(listOf(updateItem.copy(isFollowed = true)))
        } ?: database.listsDao().insertAll(listOf(result))
        return result
    }
}
