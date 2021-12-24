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
package com.twidere.twiderex.dataprovider

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.sqldelight.SqlDelightAppDatabaseImpl
import com.twidere.twiderex.db.sqldelight.SqlDelightCacheDatabaseImpl
import com.twidere.twiderex.db.sqldelight.createAppDataBase
import com.twidere.twiderex.db.sqldelight.createCacheDataBase
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.kmp.StorageProvider
import com.twidere.twiderex.kmp.appFiles
import com.twidere.twiderex.kmp.cacheFiles

actual class DataProvider {
    // data provide functions....
    private val JCDB_PREFIX = "jdbc:sqlite:"
    private val APP_DATABASE = "$JCDB_PREFIX${get<StorageProvider>().appFiles.databaseFile("app.db")}"
    private val CACHE_DATABASE = "$JCDB_PREFIX${get<StorageProvider>().cacheFiles.databaseFile("cache.db")}"
    actual companion object Factory {
        actual fun create(): DataProvider {
            return DataProvider()
        }
    }

    internal val realAppDatabase = createAppDataBase(JdbcSqliteDriver(APP_DATABASE))

    actual val appDatabase: AppDatabase
        get() = SqlDelightAppDatabaseImpl(
            database = realAppDatabase
        )

    actual val cacheDatabase: CacheDatabase
        get() = SqlDelightCacheDatabaseImpl(
            database = createCacheDataBase(JdbcSqliteDriver(CACHE_DATABASE))
        )
}
