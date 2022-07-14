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
package com.twidere.twiderex.db.sqldelight.dao

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import com.twidere.twiderex.db.dao.DraftDao
import com.twidere.twiderex.db.sqldelight.transform.toDbDraft
import com.twidere.twiderex.db.sqldelight.transform.toUi
import com.twidere.twiderex.model.ui.UiDraft
import com.twidere.twiderex.sqldelight.table.Draft
import com.twidere.twiderex.sqldelight.table.DraftQueries
import kotlinx.coroutines.flow.map

internal class SqlDelightDraftDaoImpl(private val queries: DraftQueries) : DraftDao {
    override fun getAll() = queries.getAll().asUiFlow()

    override fun getDraftCount() = queries.getDraftCount().asFlow().mapToOneNotNull()

    override suspend fun insert(it: UiDraft) = queries.insert(it.toDbDraft())

    override suspend fun get(draftId: String) = queries.get(draftId).executeAsOneOrNull()?.toUi()

    override suspend fun remove(draft: UiDraft) = queries.remove(draft.draftId)

    private fun Query<Draft>.asUiFlow() = asFlow()
        .mapToList()
        .map { it.map { draft -> draft.toUi() } }
}
