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
package com.twidere.twiderex.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.twidere.twiderex.db.dao.DraftDao
import com.twidere.twiderex.db.dao.SearchDao
import com.twidere.twiderex.db.model.DbDraft
import com.twidere.twiderex.db.model.DbSearch
import com.twidere.twiderex.db.model.converter.ComposeTypeConverter
import com.twidere.twiderex.db.model.converter.MicroBlogKeyConverter
import com.twidere.twiderex.db.model.converter.StringListConverter
import javax.inject.Singleton

@Singleton
@Database(
    entities = [
        DbDraft::class,
        DbSearch::class,
    ],
    version = 3,
)
@TypeConverters(
    MicroBlogKeyConverter::class,
    ComposeTypeConverter::class,
    StringListConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun draftDao(): DraftDao
    abstract fun searchDao(): SearchDao
}

val AppDatabase_Migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `search` (`_id` TEXT NOT NULL, `content` TEXT NOT NULL, `lastActive` INTEGER NOT NULL, PRIMARY KEY(`_id`))")
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_search_content` ON `search` (`content`)")
    }
}

val AppDatabase_Migration_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `search` ADD COLUMN `saved` INTEGER DEFAULT 0 NOT NULL")
    }
}
