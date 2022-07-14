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

import android.content.Context
import androidx.room.Room
import com.twidere.twiderex.dataprovider.db.AppDatabaseImpl
import com.twidere.twiderex.dataprovider.db.CacheDatabaseImpl
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.room.db.AppDatabase_Migration_1_2
import com.twidere.twiderex.room.db.AppDatabase_Migration_2_3
import com.twidere.twiderex.room.db.RoomAppDatabase
import com.twidere.twiderex.room.db.RoomCacheDatabase

actual class DataProvider private constructor(context: Context) {
    // data provide functions....
    actual companion object Factory {
        actual fun create(): DataProvider {
            return DataProvider(get())
        }
    }

    private val roomCacheDatabase = Room.databaseBuilder(context, RoomCacheDatabase::class.java, "twiderex-db")
        .fallbackToDestructiveMigration()
        .build()

    private val roomAppDatabase = Room.databaseBuilder(context, RoomAppDatabase::class.java, "twiderex-draft-db")
        .addMigrations(AppDatabase_Migration_1_2)
        .addMigrations(AppDatabase_Migration_2_3)
        .build()

    actual val appDatabase: AppDatabase = AppDatabaseImpl(roomAppDatabase)

    actual val cacheDatabase: CacheDatabase = CacheDatabaseImpl(roomCacheDatabase)
}
