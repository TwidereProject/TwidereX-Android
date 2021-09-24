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

import com.twidere.twiderex.mock.model.mockUiUrlEntity
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.sqldelight.table.UrlEntity
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class UrlEntityTransformTest {
    @Test
    fun dbToUi() {
        val db = UrlEntity(
            belongToKey = MicroBlogKey.valueOf("test"),
            url = "url",
            displayUrl = "displayUrl",
            id = UUID.randomUUID().toString(),
            expandedUrl = "expandedUrl",
            title = "title",
            description = "description",
            image = "image"
        )
        val ui = db.toUi()
        assertEquals(db.url, ui.url)
        assertEquals(db.url, ui.url)
        assertEquals(db.displayUrl, ui.displayUrl)
        assertEquals(db.expandedUrl, ui.expandedUrl)
        assertEquals(db.title, ui.title)
        assertEquals(db.description, ui.description)
    }

    @Test
    fun uiToDb() {
        val belongToKey = MicroBlogKey.valueOf("test")
        val id = UUID.randomUUID().toString()
        val ui = mockUiUrlEntity(url = "url")
        val db = ui.toDbUrlEntity(belongToKey = belongToKey, id = id)
        assertEquals(db.url, ui.url)
        assertEquals(db.url, ui.url)
        assertEquals(db.displayUrl, ui.displayUrl)
        assertEquals(db.expandedUrl, ui.expandedUrl)
        assertEquals(db.title, ui.title)
        assertEquals(db.description, ui.description)
        assertEquals(id, db.id)
        assertEquals(belongToKey, db.belongToKey)
    }
}
