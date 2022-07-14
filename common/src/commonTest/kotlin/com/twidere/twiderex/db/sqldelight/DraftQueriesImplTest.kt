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
package com.twidere.twiderex.db.sqldelight

import com.twidere.twiderex.base.BaseAppDatabaseTest
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.sqldelight.table.Draft
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class DraftQueriesImplTest : BaseAppDatabaseTest() {
    @Test
    fun insertDraft_InsertOrReplaceWhenIdEquals() = runBlocking {
        val query = database.draftQueries
        val insert = createDraft(UUID.randomUUID().toString())
        query.insert(insert)
        var result = query.getAll().executeAsList()
        assertEquals(1, result.size)
        assertEquals("test", result.firstOrNull()?.content)
        query.insert(insert.copy(content = "replace"))
        result = query.getAll().executeAsList()
        assertEquals(1, result.size)
        assertEquals("replace", result.firstOrNull()?.content)
    }

    @Test
    fun get_getAllReturnsAllDraftAndGetReturnsDraftWithGivenId() = runBlocking {
        val query = database.draftQueries
        val specifiedId = UUID.randomUUID().toString()
        query.transaction {
            for (i in 0 until 10) {
                query.insert(createDraft(UUID.randomUUID().toString()))
            }
            query.insert(createDraft(specifiedId))
        }
        assertEquals(11, query.getAll().executeAsList().size)
        assertEquals(specifiedId, query.get(specifiedId).executeAsOneOrNull()?.id)
    }

    @Test
    fun remove_removeDraftWithGivenId() = runBlocking {
        val query = database.draftQueries
        val specifiedId = UUID.randomUUID().toString()
        query.transaction {
            for (i in 0 until 10) {
                query.insert(createDraft(UUID.randomUUID().toString()))
            }
            query.insert(createDraft(specifiedId))
        }
        assertEquals(11, query.getAll().executeAsList().size)
        assertEquals(specifiedId, query.get(specifiedId).executeAsOneOrNull()?.id)
        query.remove(specifiedId)
        assertNull(query.get(specifiedId).executeAsOneOrNull())
        assertEquals(10, query.getAll().executeAsList().size)
    }

    @Test
    fun getDraftCount_ReturnsCountOfAllDrafts() = runBlocking {
        val query = database.draftQueries
        query.transaction {
            for (i in 0 until 10) {
                query.insert(createDraft(UUID.randomUUID().toString()))
            }
        }
        assertEquals(10, query.getDraftCount().executeAsOneOrNull())
    }

    private fun createDraft(id: String) = Draft(
        id = id,
        content = "test",
        media = listOf(),
        createAt = System.currentTimeMillis(),
        composeType = ComposeType.New,
        statusKey = null,
        excludedReplyUserIds = null
    )
}
