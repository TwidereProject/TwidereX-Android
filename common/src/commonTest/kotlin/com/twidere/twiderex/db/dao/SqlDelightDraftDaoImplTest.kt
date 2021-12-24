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
package com.twidere.twiderex.db.dao

import com.twidere.twiderex.base.BaseAppDatabaseTest
import com.twidere.twiderex.db.sqldelight.dao.SqlDelightDraftDaoImpl
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.ui.UiDraft
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class SqlDelightDraftDaoImplTest : BaseAppDatabaseTest() {
    @Test
    fun getAll_ReturnsFlowAndUpdateAfterDbUpdated() = runBlocking {
        val draftDao = SqlDelightDraftDaoImpl(database.draftQueries)
        val flow = draftDao.getAll()
        assert(flow.firstOrNull()?.isEmpty() ?: false)
        val draft = createUiDraft()
        draftDao.insert(draft)
        assert(flow.firstOrNull()?.isNotEmpty() ?: false)
        draftDao.remove(draft)
        assert(flow.firstOrNull()?.isEmpty() ?: true)
    }

    @Test
    fun getDraftCount_ReturnsFlowAndUpdateAfterDbUpdated() = runBlocking {
        val draftDao = SqlDelightDraftDaoImpl(database.draftQueries)
        val flow = draftDao.getDraftCount()
        assertEquals(0L, flow.firstOrNull() ?: false)
        val draft = createUiDraft()
        draftDao.insert(draft)
        assertEquals(1L, flow.firstOrNull() ?: false)
        draftDao.remove(draft)
        assertEquals(0L, flow.firstOrNull() ?: false)
    }

    private fun createUiDraft(
        id: String = UUID.randomUUID().toString(),
        content: String = UUID.randomUUID().toString(),
    ) = UiDraft(
        draftId = id,
        content = content,
        media = emptyList(),
        createdAt = System.currentTimeMillis(),
        composeType = ComposeType.New,
        statusKey = null,
        excludedReplyUserIds = null
    )
}
