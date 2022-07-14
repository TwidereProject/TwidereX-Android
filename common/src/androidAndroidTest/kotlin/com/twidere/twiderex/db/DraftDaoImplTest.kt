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
package com.twidere.twiderex.db

import com.twidere.twiderex.dataprovider.db.AppDatabaseImpl
import com.twidere.twiderex.db.base.AppDatabaseDaoTest
import com.twidere.twiderex.mock.model.mockUiDraft
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class DraftDaoImplTest : AppDatabaseDaoTest() {

    @Test
    fun getAll_returnResultFlow(): Unit = runBlocking {
        val appDatabase = AppDatabaseImpl(roomDatabase)
        val resultFlow = appDatabase.draftDao().getAll()
        assertEquals(0, resultFlow.firstOrNull()?.size)
        val removeDraft = mockUiDraft()
        appDatabase.withTransaction {
            appDatabase.draftDao().insert(removeDraft)
            appDatabase.draftDao().insert(mockUiDraft())
        }
        assertEquals(2, resultFlow.firstOrNull()?.size)

        appDatabase.draftDao().remove(removeDraft)

        assertNull(appDatabase.draftDao().get(removeDraft.draftId))
    }

    @Test
    fun getDraftCount_returnCorrectCountFlow(): Unit = runBlocking {
        val appDatabase = AppDatabaseImpl(roomDatabase)
        val resultFlow = appDatabase.draftDao().getDraftCount()
        assertEquals(0, resultFlow.firstOrNull())
        appDatabase.withTransaction {
            appDatabase.draftDao().insert(mockUiDraft())
            appDatabase.draftDao().insert(mockUiDraft())
        }
        assertEquals(2, resultFlow.firstOrNull())
    }
}
