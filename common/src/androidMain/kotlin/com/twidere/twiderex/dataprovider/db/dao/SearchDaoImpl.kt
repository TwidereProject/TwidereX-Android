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

import androidx.room.withTransaction
import com.twidere.twiderex.db.dao.SearchDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiSearch
import com.twidere.twiderex.room.db.RoomAppDatabase
import com.twidere.twiderex.room.db.transform.toDbSearch
import com.twidere.twiderex.room.db.transform.toUiSearch
import kotlinx.coroutines.flow.map

internal class SearchDaoImpl(private val roomAppDatabase: RoomAppDatabase) : SearchDao {
    override suspend fun insertAll(search: List<UiSearch>) = roomAppDatabase.searchDao().insertAll(search.map { it.toDbSearch() })

    override fun getAll(accountKey: MicroBlogKey) = roomAppDatabase.searchDao().getAll(accountKey).map { list -> list.map { it.toUiSearch() } }

    override fun getAllHistory(accountKey: MicroBlogKey) = roomAppDatabase.searchDao().getAllHistory(accountKey).map { list -> list.map { it.toUiSearch() } }

    override fun getAllSaved(accountKey: MicroBlogKey) = roomAppDatabase.searchDao().getAllSaved(accountKey).map { list -> list.map { it.toUiSearch() } }

    override suspend fun get(content: String, accountKey: MicroBlogKey) = roomAppDatabase.searchDao().get(content, accountKey)?.toUiSearch()

    override suspend fun remove(search: UiSearch) {
        roomAppDatabase.withTransaction {
            roomAppDatabase.searchDao().get(content = search.content, accountKey = search.accountKey)
                ?.let {
                    roomAppDatabase.searchDao().remove(search.toDbSearch(id = it._id))
                }
        }
    }

    override suspend fun clear() = roomAppDatabase.searchDao().clear()
}
