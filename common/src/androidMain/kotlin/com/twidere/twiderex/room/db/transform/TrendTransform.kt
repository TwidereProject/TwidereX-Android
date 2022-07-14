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
package com.twidere.twiderex.room.db.transform

import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.model.ui.UiTrendHistory
import com.twidere.twiderex.room.db.model.DbTrend
import com.twidere.twiderex.room.db.model.DbTrendHistory
import com.twidere.twiderex.room.db.model.DbTrendWithHistory
import java.util.UUID

internal fun DbTrendWithHistory.toUi(accountKey: MicroBlogKey) = UiTrend(
    trendKey = trend.trendKey,
    displayName = trend.displayName,
    url = trend.url,
    query = trend.query,
    volume = trend.volume,
    history = history.map {
        it.toUi()
    },
    accountKey = accountKey
)

internal fun DbTrendHistory.toUi() = UiTrendHistory(
    trendKey = trendKey,
    day = day,
    uses = uses,
    accounts = accounts
)

internal fun List<UiTrend>.toDbTrendWithHistory() = map {
    DbTrendWithHistory(
        trend = it.toDbTrend(),
        history = it.history.map { history -> history.toDbTrendHistory(it.accountKey) }
    )
}

internal fun UiTrend.toDbTrend() = DbTrend(
    _id = UUID.randomUUID().toString(),
    trendKey = trendKey,
    displayName = displayName,
    url = url,
    query = query,
    volume = volume,
    accountKey = accountKey
)

internal fun UiTrendHistory.toDbTrendHistory(accountKey: MicroBlogKey) = DbTrendHistory(
    trendKey = trendKey,
    day = day,
    uses = uses,
    accounts = accounts,
    _id = UUID.randomUUID().toString(),
    accountKey = accountKey
)
