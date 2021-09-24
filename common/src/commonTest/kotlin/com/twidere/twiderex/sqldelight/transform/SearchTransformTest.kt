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
package com.twidere.twiderex.sqldelight.transform

import com.twidere.twiderex.db.sqldelight.transform.toDbSearch
import com.twidere.twiderex.db.sqldelight.transform.toUi
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiSearch
import com.twidere.twiderex.sqldelight.table.Search
import org.junit.Test
import kotlin.test.assertEquals

class SearchTransformTest {
    @Test
    fun searchToUi() {
        val search = Search(
            content = "search",
            lastActive = System.currentTimeMillis(),
            accountKey = MicroBlogKey.valueOf("test"),
            saved = true
        )
        val uiSearch = search.toUi()
        assertEquals(search.content, uiSearch.content)
        assertEquals(search.lastActive, uiSearch.lastActive)
        assertEquals(search.accountKey, uiSearch.accountKey)
        assertEquals(search.saved, uiSearch.saved)
    }

    @Test
    fun uiTosearch() {
        val uiSearch = UiSearch(
            content = "search",
            lastActive = System.currentTimeMillis(),
            accountKey = MicroBlogKey.valueOf("test"),
            saved = true
        )
        val search = uiSearch.toDbSearch()
        assertEquals(search.content, uiSearch.content)
        assertEquals(search.lastActive, uiSearch.lastActive)
        assertEquals(search.accountKey, uiSearch.accountKey)
        assertEquals(search.saved, uiSearch.saved)
    }
}
