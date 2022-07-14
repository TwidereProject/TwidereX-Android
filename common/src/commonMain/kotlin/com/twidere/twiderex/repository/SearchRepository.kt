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
package com.twidere.twiderex.repository

import androidx.paging.flatMap
import androidx.paging.map
import com.twidere.services.microblog.SearchService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiSearch
import com.twidere.twiderex.paging.mediator.paging.pager
import com.twidere.twiderex.paging.mediator.search.SearchMediaMediator
import kotlinx.coroutines.flow.map

class SearchRepository(
    private val database: AppDatabase,
    private val cacheDatabase: CacheDatabase,
) {
    fun searchHistory(accountKey: MicroBlogKey) = database.searchDao().getAllHistory(accountKey)

    fun savedSearch(accountKey: MicroBlogKey) = database.searchDao().getAllSaved(accountKey)

    fun media(keyword: String, accountKey: MicroBlogKey, service: SearchService) =
        SearchMediaMediator(keyword, cacheDatabase, accountKey, service)
            .pager()
            .flow.map { it.map { it.status } }
            .map {
                it.flatMap {
                    it.media.map { media -> media to it }
                }
            }

    suspend fun addOrUpgrade(
        content: String,
        accountKey: MicroBlogKey,
        saved: Boolean = false
    ) {
        val search = database.searchDao().get(content, accountKey)?.let {
            it.copy(
                lastActive = System.currentTimeMillis(),
                saved = if (it.saved) it.saved else saved
            )
        } ?: UiSearch(
            content = content,
            lastActive = System.currentTimeMillis(),
            saved = saved,
            accountKey = accountKey
        )
        database.searchDao().insertAll(
            listOf(search)
        )
    }

    suspend fun remove(item: UiSearch) {
        database.searchDao().remove(item)
    }

    suspend fun get(content: String, accountKey: MicroBlogKey): UiSearch? {
        return database.searchDao().get(content, accountKey)
    }
}
