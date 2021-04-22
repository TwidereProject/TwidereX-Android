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
package com.twidere.twiderex.repository

import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbDraft
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.viewmodel.compose.ComposeType
import java.util.UUID
import javax.inject.Singleton

@Singleton
class DraftRepository(
    private val database: AppDatabase
) {
    val source by lazy {
        database.draftDao().getAll()
    }

    suspend fun addOrUpgrade(
        content: String,
        media: List<String>,
        composeType: ComposeType,
        statusKey: MicroBlogKey?,
        draftId: String = UUID.randomUUID().toString(),
        excludedReplyUserIds: List<String>? = null,
    ) {
        DbDraft(
            _id = draftId,
            content = content,
            composeType = composeType,
            media = media,
            statusKey = statusKey,
            createdAt = System.currentTimeMillis(),
            excludedReplyUserIds = excludedReplyUserIds
        ).let {
            database.draftDao().insertAll(it)
        }
    }

    suspend fun get(draftId: String): DbDraft? {
        return database.draftDao().get(draftId)
    }

    suspend fun remove(draftId: String) {
        database.draftDao().get(draftId)?.let {
            remove(it)
        }
    }

    suspend fun remove(draft: DbDraft) {
        database.draftDao().remove(draft)
    }
}
