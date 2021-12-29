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

import androidx.datastore.core.DataStore
import androidx.paging.PagingData
import com.twidere.services.microblog.TrendService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.paging.mediator.trend.TrendMediator
import com.twidere.twiderex.paging.mediator.trend.TrendMediator.Companion.toUi
import com.twidere.twiderex.preferences.model.DisplayPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class TrendRepository(
    private val database: CacheDatabase,
    private val preferences: DataStore<DisplayPreferences>?,
) {
    private val worldWideId = "1"

    fun trendsSource(
        accountKey: MicroBlogKey,
        service: TrendService,
        locationId: String = worldWideId
    ): Flow<PagingData<UiTrend>> {
        val mediator = TrendMediator(
            database = database,
            service = service,
            accountKey = accountKey,
            locationId = locationId
        )
        return flow {
            emitAll(mediator.pager(pageSize = getPageSize()).toUi())
        }
    }

    private suspend fun getPageSize(): Int {
        return preferences?.data?.first()?.loadItemLimit ?: defaultLoadCount
    }
}
