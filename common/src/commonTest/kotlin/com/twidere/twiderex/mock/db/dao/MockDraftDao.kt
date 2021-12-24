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
package com.twidere.twiderex.mock.db.dao

import com.twidere.twiderex.db.dao.DraftDao
import com.twidere.twiderex.model.ui.UiDraft
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.annotations.TestOnly

internal class MockDraftDao @TestOnly constructor() : DraftDao {
    private val fakeDb = mutableMapOf<String, UiDraft>()
    override fun getAll(): Flow<List<UiDraft>> {
        return flow {
            emit(fakeDb.values.toList())
        }
    }

    override fun getDraftCount(): Flow<Long> {
        return flow {
            emit(fakeDb.keys.size.toLong())
        }
    }

    override suspend fun insert(it: UiDraft) {
        fakeDb[it.draftId] = it
    }

    override suspend fun get(draftId: String): UiDraft? {
        return fakeDb[draftId]
    }

    override suspend fun remove(draft: UiDraft) {
        fakeDb.remove(draft.draftId)
    }
}
