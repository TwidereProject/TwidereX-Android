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
package com.twidere.twiderex.db.dao

import com.twidere.twiderex.base.BaseAppDatabaseTest
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightSearchDaoImpl
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiSearch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class SqlDelightSearchDaoImplTest : BaseAppDatabaseTest() {
    @Test
    fun getAll_ReturnsFlowAndUpdateAfterDbUpdated() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val searchDao = SqlDelightSearchDaoImpl(database.searchQueries)
        val flow = searchDao.getAll(accountKey)
        assert(flow.firstOrNull()?.isEmpty() ?: false)
        searchDao.insertAll(createSearchList(accountKey = accountKey, count = 1))
        assert(flow.firstOrNull()?.isNotEmpty() ?: false)
    }

    @Test
    fun insertAll_InsertAllDataInGivenList() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val searchDao = SqlDelightSearchDaoImpl(database.searchQueries)
        val flow = searchDao.getAll(accountKey)
        assert(flow.firstOrNull()?.isEmpty() ?: false)
        val count = 10
        searchDao.insertAll(createSearchList(count = count, accountKey = accountKey))
        assertEquals(count, flow.firstOrNull()?.size)
    }

    @Test
    fun getHistories_ReturnsFlowWithNotSavedSearchAndUpdateAfterDbUpdated() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val searchDao = SqlDelightSearchDaoImpl(database.searchQueries)
        val flow = searchDao.getAllHistory(accountKey)
        assert(flow.firstOrNull()?.isEmpty() ?: false)
        searchDao.insertAll(createSearchList(count = 5, accountKey = accountKey))
        searchDao.insertAll(createSearchList(count = 5, accountKey = accountKey, saved = true))
        assertEquals(5, flow.firstOrNull()?.size)
        flow.firstOrNull()?.forEach {
            assertEquals(false, it.saved)
        }
        searchDao.insertAll(flow.firstOrNull()!!.map { it.copy(saved = true) })
        assert(flow.firstOrNull()?.isEmpty() ?: true)
        searchDao.clear()
        assert(flow.firstOrNull()?.isEmpty() ?: true)
    }

    @Test
    fun getSaved_ReturnsFlowWithSavedSearchAndUpdateAfterDbUpdated() = runBlocking {
        val accountKey = MicroBlogKey.twitter("test")
        val searchDao = SqlDelightSearchDaoImpl(database.searchQueries)
        val flow = searchDao.getAllSaved(accountKey)
        assert(flow.firstOrNull()?.isEmpty() ?: false)
        searchDao.insertAll(createSearchList(count = 5, accountKey = accountKey))
        searchDao.insertAll(createSearchList(count = 5, accountKey = accountKey, saved = true))
        assertEquals(5, flow.firstOrNull()?.size)
        flow.firstOrNull()?.forEach {
            assertEquals(true, it.saved)
        }
        searchDao.insertAll(flow.firstOrNull()!!.map { it.copy(saved = false) })
        assert(flow.firstOrNull()?.isEmpty() ?: true)
    }

    private fun createSearchList(
        count: Int,
        content: String = UUID.randomUUID().toString(),
        accountKey: MicroBlogKey,
        saved: Boolean = false
    ): MutableList<UiSearch> {
        val list = mutableListOf<UiSearch>()
        for (i in 0 until count) {
            list.add(
                UiSearch(
                    content = content + i.toString(),
                    accountKey = accountKey,
                    lastActive = System.currentTimeMillis(),
                    saved = saved
                )
            )
        }
        return list
    }
}
