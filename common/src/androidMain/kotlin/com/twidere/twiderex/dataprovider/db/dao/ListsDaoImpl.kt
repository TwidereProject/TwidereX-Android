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
package com.twidere.twiderex.dataprovider.db.dao

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.twidere.twiderex.db.dao.ListsDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.room.db.RoomCacheDatabase
import com.twidere.twiderex.room.db.paging.getPagingSource
import com.twidere.twiderex.room.db.transform.toDbList
import com.twidere.twiderex.room.db.transform.toUi
import kotlinx.coroutines.flow.map

internal class ListsDaoImpl(private val database: RoomCacheDatabase) : ListsDao {
    override fun getPagingSource(accountKey: MicroBlogKey): PagingSource<Int, UiList> {
        return database.listsDao().getPagingSource(cacheDatabase = database, accountKey = accountKey)
    }

    override fun findWithListKeyWithFlow(
        listKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ) = database.listsDao().findWithListKeyWithFlow(listKey, accountKey).map { it?.toUi() }

    override suspend fun insertAll(listOf: List<UiList>) {
        database.listsDao().insertAll(listOf.map { it.toDbList() })
    }

    override suspend fun findWithListKey(
        listKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ) = database.listsDao().findWithListKey(listKey, accountKey)?.toUi()

    override suspend fun update(listOf: List<UiList>) {
        database.withTransaction {
            listOf.mapNotNull {
                database.listsDao().findWithListKey(it.listKey, it.accountKey)?.let { dbList ->
                    it.toDbList(dbId = dbList._id)
                }
            }.let {
                database.listsDao().update(it)
            }
        }
    }

    override suspend fun delete(listOf: List<UiList>) {
        database.withTransaction {
            listOf.mapNotNull {
                database.listsDao().findWithListKey(it.listKey, it.accountKey)?.let { dbList ->
                    it.toDbList(dbId = dbList._id)
                }
            }.let {
                database.listsDao().delete(it)
            }
        }
    }

    override suspend fun clearAll(accountKey: MicroBlogKey) {
        database.listsDao().clearAll(accountKey)
    }
}
