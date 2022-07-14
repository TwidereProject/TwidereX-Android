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
package com.twidere.twiderex.db.sqldelight.transform

import com.twidere.twiderex.db.sqldelight.model.DbTrendWithHistory
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.model.ui.UiTrendHistory
import com.twidere.twiderex.sqldelight.table.DbTrend
import com.twidere.twiderex.sqldelight.table.DbTrendHistory

internal fun UiTrendHistory.toDbTrendHistory(accountKey: MicroBlogKey) = DbTrendHistory(
    trendKey = trendKey,
    accountKey = accountKey,
    day = day,
    uses = uses,
    accounts = accounts
)

internal fun UiTrend.toDbTrendWithHistory() = DbTrendWithHistory(
    trend = DbTrend(
        trendKey = trendKey,
        accountKey = accountKey,
        displayName = displayName,
        url = url,
        query = query,
        volume = volume
    ),
    history = history.map { it.toDbTrendHistory(accountKey) }
)

internal fun DbTrendHistory.toUi() = UiTrendHistory(
    trendKey = trendKey,
    day = day,
    uses = uses,
    accounts = accounts
)

internal fun DbTrendWithHistory.toUi() = UiTrend(
    trendKey = trend.trendKey,
    accountKey = trend.accountKey,
    displayName = trend.displayName,
    url = trend.url,
    query = trend.query,
    volume = trend.volume,
    history = history.map { it.toUi() }
)
