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

import com.twidere.twiderex.db.dao.SearchDao
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiSearch
import com.twidere.twiderex.room.db.dao.RoomSearchDao
import com.twidere.twiderex.room.db.transform.toDbSearch
import com.twidere.twiderex.room.db.transform.toUiSearch
import kotlinx.coroutines.flow.map

internal class SearchDaoImpl(private val roomSearchDao: RoomSearchDao) : SearchDao {
    override suspend fun insertAll(search: List<UiSearch>) = roomSearchDao.insertAll(search.map { it.toDbSearch() })

    override fun getAll(accountKey: MicroBlogKey) = roomSearchDao.getAll(accountKey).map { list -> list.map { it.toUiSearch() } }

    override fun getAllHistory(accountKey: MicroBlogKey) = roomSearchDao.getAllHistory(accountKey).map { list -> list.map { it.toUiSearch() } }

    override fun getAllSaved(accountKey: MicroBlogKey) = roomSearchDao.getAllSaved(accountKey).map { list -> list.map { it.toUiSearch() } }

    override suspend fun get(content: String, accountKey: MicroBlogKey) = roomSearchDao.get(content, accountKey)?.toUiSearch()

    override suspend fun remove(search: UiSearch) = roomSearchDao.remove(search.toDbSearch())

    override suspend fun clear() = roomSearchDao.clear()
}
