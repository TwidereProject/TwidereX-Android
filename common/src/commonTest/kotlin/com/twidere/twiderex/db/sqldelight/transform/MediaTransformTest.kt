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
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.sqldelight.table.Media
import org.junit.Test
import kotlin.test.assertEquals

internal class MediaTransformTest {
    @Test
    fun mediaToUi() {
        val db = Media(
            belongToKey = MicroBlogKey.valueOf("test"),
            url = "url",
            mediaUrl = "mediaUrl",
            previewUrl = "previewUrl",
            type = MediaType.video,
            width = 100,
            height = 100,
            pageUrl = "pageUrl",
            altText = "altText",
            orderIndex = 5
        )
        val ui = db.toUi()
        assertEquals(db.belongToKey, ui.belongToKey)
        assertEquals(db.url, ui.url)
        assertEquals(db.mediaUrl, ui.mediaUrl)
        assertEquals(db.type, ui.type)
        assertEquals(db.width, ui.width)
        assertEquals(db.height, ui.height)
        assertEquals(db.pageUrl, ui.pageUrl)
        assertEquals(db.altText, ui.altText)
        assertEquals(db.orderIndex.toInt(), ui.order)
    }

    @Test
    fun uiToMedia() {
        val ui = UiMedia(
            belongToKey = MicroBlogKey.valueOf("test"),
            url = "url",
            mediaUrl = "mediaUrl",
            previewUrl = "previewUrl",
            type = MediaType.video,
            width = 100,
            height = 100,
            pageUrl = "pageUrl",
            altText = "altText",
            order = 5
        )
        val db = ui.toDbMedia()
        assertEquals(db.belongToKey, ui.belongToKey)
        assertEquals(db.url, ui.url)
        assertEquals(db.mediaUrl, ui.mediaUrl)
        assertEquals(db.type, ui.type)
        assertEquals(db.width, ui.width)
        assertEquals(db.height, ui.height)
        assertEquals(db.pageUrl, ui.pageUrl)
        assertEquals(db.altText, ui.altText)
        assertEquals(db.orderIndex.toInt(), ui.order)
    }
}
