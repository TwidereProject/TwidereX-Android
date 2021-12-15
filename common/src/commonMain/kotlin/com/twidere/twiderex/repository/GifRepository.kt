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

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.twidere.services.gif.GifService
import com.twidere.services.microblog.DownloadMediaService
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.kmp.FileResolver
import com.twidere.twiderex.paging.source.gif.GifSearchPagingSource
import com.twidere.twiderex.paging.source.gif.GifTrendingPagingSource

class GifRepository(
    private val fileResolver: FileResolver
) {
    fun gifTrending(service: GifService) = Pager(
        config = PagingConfig(
            pageSize = defaultLoadCount,
            enablePlaceholders = false,
        )
    ) {
        GifTrendingPagingSource(
            service
        )
    }.flow

    fun gifSearch(
        service: GifService,
        query: String,
        lang: String
    ) = Pager(
        config = PagingConfig(
            pageSize = defaultLoadCount,
            enablePlaceholders = false,
        )
    ) {
        GifSearchPagingSource(
            service,
            query = query,
            lang = lang
        )
    }.flow

    suspend fun download(target: String, source: String, service: DownloadMediaService) {
        fileResolver.openOutputStream(target)?.use {
            service.download(target = source).copyTo(it)
        } ?: throw Error("Download failed")
    }
}
