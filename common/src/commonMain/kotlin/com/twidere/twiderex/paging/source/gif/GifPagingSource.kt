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
package com.twidere.twiderex.paging.source.gif

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.twidere.services.gif.GifService
import com.twidere.services.gif.model.GifPaging
import com.twidere.twiderex.dataprovider.mapper.toUi
import com.twidere.twiderex.model.ui.UiGif

abstract class GifPagingSource(protected val service: GifService) : PagingSource<String, UiGif>() {
    override fun getRefreshKey(state: PagingState<String, UiGif>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, UiGif> {
        return try {
            val result = loadFromService(params.key, params.loadSize)
            val nextPage = result.nextPage
            LoadResult.Page(data = result.map { it.toUi() }, prevKey = null, nextKey = nextPage)
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }

    abstract suspend fun loadFromService(key: String?, loadSize: Int): GifPaging
}
