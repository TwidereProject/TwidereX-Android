/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.repository

import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbDraft
import com.twidere.twiderex.scenes.ComposeType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Singleton

@Singleton
class DraftRepository(
    private val database: AppDatabase
) {
    val source by lazy {
        database.draftDao().getAll()
    }
    fun addOrUpgrade(
        content: String,
        media: List<String>,
        composeType: ComposeType,
        statusId: String?,
        draftId: String = UUID.randomUUID().toString(),
        excludedReplyUserIds: List<String>? = null,
    ) {
        GlobalScope.launch {
            DbDraft(
                _id = draftId,
                content = content,
                composeType = composeType,
                media = media,
                statusId = statusId,
                createdAt = System.currentTimeMillis(),
                excludedReplyUserIds = excludedReplyUserIds
            ).let {
                database.draftDao().insertAll(it)
            }
        }
    }

    suspend fun get(draftId: String): DbDraft? {
        return database.draftDao().get(draftId)
    }

    fun remove(draftId: String) {
        GlobalScope.launch {
            database.draftDao().get(draftId)?.let {
                remove(it)
            }
        }
    }

    fun remove(draft: DbDraft) {
        GlobalScope.launch {
            database.draftDao().remove(draft)
        }
    }
}
