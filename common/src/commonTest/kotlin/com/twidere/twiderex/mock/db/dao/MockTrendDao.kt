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
package com.twidere.twiderex.mock.db.dao

import androidx.paging.PagingSource
import com.twidere.twiderex.db.dao.TrendDao
import com.twidere.twiderex.mock.paging.MockPagingSource
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiTrend
import org.jetbrains.annotations.TestOnly

class MockTrendDao @TestOnly constructor() : TrendDao {
    private val fakeDb = mutableMapOf<MicroBlogKey, MutableList<UiTrend>>()

    override suspend fun insertAll(trends: List<UiTrend>) {
        trends.forEach { uiTrend ->
            fakeDb[uiTrend.accountKey].let {
                if (it.isNullOrEmpty()) {
                    fakeDb[uiTrend.accountKey] = mutableListOf(uiTrend)
                } else {
                    it.add(uiTrend)
                }
            }
        }
    }

    override fun getPagingSource(accountKey: MicroBlogKey): PagingSource<Int, UiTrend> {
        return MockPagingSource(
            fakeDb[accountKey] ?: emptyList()
        )
    }

    override suspend fun clear(accountKey: MicroBlogKey) {
        fakeDb.clear()
    }
}
