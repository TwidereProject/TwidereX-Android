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
package com.twidere.twiderex.dataprovider.db

import com.twidere.twiderex.dataprovider.dao.DraftDaoImpl
import com.twidere.twiderex.dataprovider.dao.SearchDaoImpl
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.sqldelight.SqlDelightAppDatabase
import kotlinx.coroutines.runBlocking

internal class AppDatabaseImpl(private val database: SqlDelightAppDatabase) : AppDatabase {
    private val draftDao = DraftDaoImpl(database.draftQueries)
    override fun draftDao() = draftDao

    private val searchDao = SearchDaoImpl(database.searchQueries)
    override fun searchDao() = searchDao

    override suspend fun clearAllTables() {
        database.dropQueries.clearAllTables()
    }

    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        // TODO find a way to handle transaction
        return database.transactionWithResult { runBlocking { block.invoke() } }
    }
}
