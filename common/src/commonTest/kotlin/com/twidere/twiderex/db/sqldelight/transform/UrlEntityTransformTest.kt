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

import com.twidere.twiderex.mock.model.mockUiUrlEntity
import com.twidere.twiderex.model.MicroBlogKey
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class UrlEntityTransformTest {
    @Test
    fun transform() {
        val belongToKey = MicroBlogKey.valueOf("test")
        val id = UUID.randomUUID().toString()
        val ui = mockUiUrlEntity(url = "url")
        val db = ui.toDbUrlEntity(belongToKey = belongToKey, id = id)
        assertEquals(ui, db.toUi())
    }
}
