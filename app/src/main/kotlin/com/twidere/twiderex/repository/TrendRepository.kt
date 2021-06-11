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

import com.twidere.services.microblog.TrendService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.db.mapper.toDbTrend
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.model.ui.UiTrend.Companion.toUi

class TrendRepository(private val database: CacheDatabase) {
    private val defaultLimit = 10
    private val worldWideId = "1"

    suspend fun trends(
        account: AccountDetails,
        locationId: String = worldWideId
    ): List<UiTrend> {
        val result = try {
            val response = (account.service as TrendService).trends(locationId, defaultLimit).map {
                it.toDbTrend(accountKey = account.accountKey)
            }
            database.trendDao().clearAll(account.accountKey)
            database.trendHistoryDao().clearAll(account.accountKey)
            response.saveToDb(database)
            response
        } catch (e: Exception) {
            database.trendDao().find(account.accountKey, defaultLimit)
        }
        return result.map { it.toUi() }
    }
}
