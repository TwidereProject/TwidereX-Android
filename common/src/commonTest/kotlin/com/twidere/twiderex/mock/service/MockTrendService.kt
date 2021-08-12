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
package com.twidere.twiderex.mock.service

import com.twidere.services.mastodon.model.Trend
import com.twidere.services.mastodon.model.TrendHistory
import com.twidere.services.microblog.MicroBlogService
import com.twidere.services.microblog.TrendService
import com.twidere.services.microblog.model.ITrend
import java.lang.IllegalArgumentException

class MockTrendService : TrendService, MicroBlogService {

    override suspend fun trends(locationId: String, limit: Int?): List<ITrend> {

        return if (locationId == "error")
            throw IllegalArgumentException("service error")
        else {
            val list = mutableListOf<Trend>()
            for (i in 0 until (limit ?: 1)) {
                list.add(
                    Trend(
                        name = "trend $i timestamp:${System.currentTimeMillis()}",
                        url = "https://trend",
                        history = mutableListOf(
                            TrendHistory(
                                accounts = "1",
                                uses = "1",
                                day = System.currentTimeMillis().toString()
                            )
                        )
                    )
                )
            }
            list
        }
    }
}
