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

import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.mock.model.mockIStatus
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.paging.PagingTimeLine
import com.twidere.twiderex.model.paging.PagingTimeLineWithStatus
import org.junit.Test
import kotlin.test.assertEquals

class PagingTimelineTransformTest {
    @Test
    fun transform() {
        val accountKey = MicroBlogKey.twitter("account")
        val ui = PagingTimeLineWithStatus(
            timeline = PagingTimeLine(
                accountKey = accountKey,
                pagingKey = "pagingKey",
                statusKey = MicroBlogKey.valueOf("statusKey"),
                timestamp = System.currentTimeMillis(),
                sortId = System.currentTimeMillis(),
                isGap = true
            ),
            status = mockIStatus().toUi(accountKey = accountKey, isGap = true)
        )
        val db = ui.toDbPagingTimelineWithStatus()
        assertEquals(ui, db.toUi())
    }
}
