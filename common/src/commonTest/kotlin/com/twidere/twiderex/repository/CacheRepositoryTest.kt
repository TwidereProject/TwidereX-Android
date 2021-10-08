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

import com.twidere.twiderex.mock.cache.MockFileCacheHandler
import com.twidere.twiderex.mock.db.MockAppDatabase
import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.mock.model.mockUiSearch
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class CacheRepositoryTest {
    @Test
    fun clearAllCachesSuccess() = runBlocking {
        val handler = MockFileCacheHandler(
            mediaCache = mutableListOf("media"),
            fileCache = mutableListOf("file"),
        )
        val cacheDatabase = MockCacheDatabase()
        val appDatabase = MockAppDatabase()
        val repository = CacheRepository(handler, cacheDatabase, appDatabase)
        appDatabase.searchDao().insertAll(listOf(mockUiSearch(accountKey = MicroBlogKey.twitter("1"))))
        val list = appDatabase.searchDao().getAll(MicroBlogKey.twitter("1")).first()
        assert(list.isNotEmpty())
        assert(!handler.isCacheCleared())
        assert(!cacheDatabase.isAllTablesCleared())

        repository.clearCacheDir()
        repository.clearDatabaseCache()
        repository.clearImageCache()
        repository.clearSearchHistory()

        assert(handler.isCacheCleared())
        assert(cacheDatabase.isAllTablesCleared())
        assert(appDatabase.searchDao().getAll(MicroBlogKey.twitter("1")).firstOrNull().isNullOrEmpty())
    }
}
