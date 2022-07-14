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

import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiTrend
import com.twidere.twiderex.model.ui.UiTrendHistory
import org.junit.Test
import kotlin.test.assertEquals

internal class TrendTransformTest {
    @Test
    fun transform() {
        val ui = UiTrend(
            accountKey = MicroBlogKey.twitter("account"),
            trendKey = MicroBlogKey.twitter("trendKey"),
            displayName = "displaName",
            query = "query",
            url = "url",
            volume = 100,
            history = listOf(
                UiTrendHistory(
                    trendKey = MicroBlogKey.twitter("trendKey"),
                    day = 123,
                    uses = 321,
                    accounts = 111
                )
            )
        )
        val db = ui.toDbTrendWithHistory()
        assertEquals(ui, db.toUi())
    }
}
