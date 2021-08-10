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

import com.twidere.twiderex.mock.cache.MockAppCacheHandler
import kotlinx.coroutines.runBlocking
import org.junit.Test

class CacheRepositoryTest {
    @Test
    fun clearAllCachesSuccess() = runBlocking {
        val handler = MockAppCacheHandler(
            mediaCache = mutableListOf("media"),
            fileCache = mutableListOf("file"),
            database = mutableListOf("database"),
            searchHistories = mutableListOf("search"),
        )
        val repository = CacheRepository(handler)
        repository.clearCacheDir()
        repository.clearDatabaseCache()
        repository.clearImageCache()
        repository.clearSearchHistory()
        assert(handler.isCacheCleared())
    }
}
