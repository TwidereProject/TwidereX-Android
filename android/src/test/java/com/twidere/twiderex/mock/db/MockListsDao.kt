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
package com.twidere.twiderex.mock.db

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.twidere.twiderex.db.dao.ListsDao
import com.twidere.twiderex.db.model.DbList
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.flow.Flow

class MockListsDao : ListsDao {
    private val fakeDatabase = mutableListOf<DbList>()
    private var pagingSource: PagingSource<Int, DbList>? = null
    override suspend fun insertAll(lists: List<DbList>) {
        fakeDatabase.addAll(0, lists)
        pagingSource?.invalidate()
    }

    override suspend fun findWithListKey(listKey: MicroBlogKey, accountKey: MicroBlogKey): DbList? {
        return fakeDatabase.find {
            it.listKey == listKey
        }
    }

    override fun findWithListKeyWithFlow(
        listKey: MicroBlogKey,
        accountKey: MicroBlogKey
    ): Flow<DbList?> {
        TODO("Not yet implemented")
    }

    override suspend fun findAll(): List<DbList>? {
        return fakeDatabase.toList()
    }

    override suspend fun findWithAccountKey(accountKey: MicroBlogKey): List<DbList>? {
        TODO("Not yet implemented")
    }

    override fun getPagingSource(accountKey: MicroBlogKey): PagingSource<Int, DbList> {
        pagingSource = object : PagingSource<Int, DbList>() {
            override fun getRefreshKey(state: PagingState<Int, DbList>): Int? {
                return null
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DbList> {
                return try {
                    val page = params.key ?: 0
                    val count = params.loadSize
                    val startIndex = page * count
                    val endIndex = page * count + count
                    val result = when {
                        endIndex <= fakeDatabase.size -> {
                            fakeDatabase.subList(startIndex, endIndex)
                        }
                        fakeDatabase.size in (startIndex + 1) until endIndex -> {
                            fakeDatabase.subList(startIndex, fakeDatabase.size)
                        }
                        else -> {
                            emptyList()
                        }
                    }
                    LoadResult.Page(result, null, if (result.isEmpty()) null else page + 1)
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }
        }
        return pagingSource!!
    }

    override suspend fun update(lists: List<DbList>) {
        lists.forEach { updateList ->
            fakeDatabase.replaceAll {
                if (it.listKey == updateList.listKey) updateList else it
            }
        }
        pagingSource?.invalidate()
    }

    override suspend fun delete(lists: List<DbList>) {
        lists.forEach { deleteList ->
            fakeDatabase.removeAll {
                it.listKey == deleteList.listKey
            }
        }
        pagingSource?.invalidate()
    }

    override suspend fun clearAll(accountKey: MicroBlogKey) {
        fakeDatabase.clear()
    }
}
