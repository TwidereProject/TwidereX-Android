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

import com.twidere.twiderex.db.sqldelight.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.model.paging.PagingTimeLine
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import com.twidere.twiderex.sqldelight.table.DbPagingTimeline

internal fun PagingTimeLine.toDbPagingTimeline() = DbPagingTimeline(
    accountKey = accountKey,
    pagingKey = pagingKey,
    statusKey = statusKey,
    timestamp = timestamp,
    sortId = sortId,
    isGap = isGap
)

internal fun DbPagingTimeline.toUi() = PagingTimeLine(
    accountKey = accountKey,
    pagingKey = pagingKey,
    statusKey = statusKey,
    timestamp = timestamp,
    sortId = sortId,
    isGap = isGap
)

internal fun PagingTimeLineWithStatus.toDbPagingTimelineWithStatus() = DbPagingTimelineWithStatus(
    timeline = timeline.toDbPagingTimeline(),
    status = status.toDbStatusWithAttachments(accountKey = timeline.accountKey)
)

internal fun DbPagingTimelineWithStatus.toUi() = PagingTimeLineWithStatus(
    timeline = timeline.toUi(),
    status = status.toUi().copy(isGap = timeline.isGap)
)
