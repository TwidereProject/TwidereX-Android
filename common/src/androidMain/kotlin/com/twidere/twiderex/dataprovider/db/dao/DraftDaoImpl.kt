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
package com.twidere.twiderex.dataprovider.db.dao

import com.twidere.twiderex.db.dao.DraftDao
import com.twidere.twiderex.model.ui.UiDraft
import com.twidere.twiderex.room.db.dao.RoomDraftDao
import com.twidere.twiderex.room.db.transform.toDbDraft
import com.twidere.twiderex.room.db.transform.toUiDraft
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DraftDaoImpl(private val roomDraftDao: RoomDraftDao) : DraftDao {
    override fun getAll(): Flow<List<UiDraft>> = roomDraftDao.getAll().map {
        it.map { dbDraft -> dbDraft.toUiDraft() }
    }

    override fun getDraftCount() = roomDraftDao.getDraftCount().map { it.toLong() }

    override suspend fun insert(it: UiDraft) = roomDraftDao.insertAll(it.toDbDraft())

    override suspend fun get(draftId: String) = roomDraftDao.get(draftId)?.toUiDraft()

    override suspend fun remove(draft: UiDraft) = roomDraftDao.remove(draft.toDbDraft())
}
