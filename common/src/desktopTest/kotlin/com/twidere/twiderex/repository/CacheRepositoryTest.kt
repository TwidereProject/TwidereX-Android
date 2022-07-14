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

import com.twidere.twiderex.kmp.Platform
import com.twidere.twiderex.kmp.StorageProvider
import com.twidere.twiderex.kmp.cacheFiles
import com.twidere.twiderex.kmp.currentPlatform
import com.twidere.twiderex.mock.db.MockAppDatabase
import com.twidere.twiderex.mock.db.MockCacheDatabase
import com.twidere.twiderex.mock.model.mockUiSearch
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.File

internal class CacheRepositoryTest {
    @Test
    fun clearDesktopCachesSuccess() = runBlocking {
        val cacheDatabase = MockCacheDatabase()
        val appDatabase = MockAppDatabase()
        val storage = StorageProvider()
        val repository = CacheRepository(storage, cacheDatabase, appDatabase)
        appDatabase.searchDao().insertAll(listOf(mockUiSearch(accountKey = MicroBlogKey.twitter("1"))))
        val list = appDatabase.searchDao().getAll(MicroBlogKey.twitter("1")).first()
        val mediaDir = File(storage.mediaCacheDir).also {
            File(it, "test").createNewFile()
        }
        val cacheDir = File(storage.cacheDir).also {
            File(it, "test").createNewFile()
        }

        assert(list.isNotEmpty())
        assert(mediaDir.listFiles()?.isNotEmpty() ?: false)
        assert(cacheDir.listFiles()?.isNotEmpty() ?: false)
        assert(!cacheDatabase.isAllTablesCleared())

        repository.clearCacheDir()
        repository.clearDatabaseCache()
        repository.clearImageCache()
        repository.clearSearchHistory()

        assert(mediaDir.listFiles()?.isEmpty() ?: true)
        if (currentPlatform == Platform.JVM) {
            assert(!File(cacheDir, "test").exists())
            assert(File(storage.cacheFiles.databaseDir).exists())
        } else {
            assert(cacheDir.listFiles()?.isEmpty() ?: true)
        }
        assert(cacheDatabase.isAllTablesCleared())
        assert(appDatabase.searchDao().getAll(MicroBlogKey.twitter("1")).firstOrNull().isNullOrEmpty())
    }
}
