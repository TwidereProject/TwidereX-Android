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
package com.twidere.twiderex.db.sqldelight.transform

import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.sqldelight.table.List
import org.junit.Test
import kotlin.test.assertEquals

internal class ListTransformTest {
    @Test
    fun transform() {
        val ui = UiList(
            accountKey = MicroBlogKey.twitter("account"),
            id = "id",
            ownerId = "ownerId",
            title = "title",
            descriptions = "desc",
            mode = "mode",
            replyPolicy = "private",
            listKey = MicroBlogKey.twitter("list"),
            isFollowed = true,
            allowToSubscribe = false
        )
        val db = ui.toDbList()
        assertSuccess(db, ui)

        val uiFromDb = db.toUi()
        assertSuccess(db, uiFromDb)
    }

    private fun assertSuccess(db: List, ui: UiList) {
        assertEquals(db.accountKey, ui.accountKey)
        assertEquals(db.listId, ui.id)
        assertEquals(db.ownerId, ui.ownerId)
        assertEquals(db.title, ui.title)
        assertEquals(db.descriptions, ui.descriptions)
        assertEquals(db.mode, ui.mode)
        assertEquals(db.replyPolicy, ui.replyPolicy)
        assertEquals(db.listKey, ui.listKey)
        assertEquals(db.isFollowed, ui.isFollowed)
        assertEquals(db.allowToSubscribe, ui.allowToSubscribe)
    }
}
