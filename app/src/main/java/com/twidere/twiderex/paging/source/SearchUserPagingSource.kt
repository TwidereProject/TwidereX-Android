package com.twidere.twiderex.paging.source

import androidx.paging.PagingSource
import com.twidere.services.microblog.SearchService
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi

class SearchUserPagingSource(
    private val query: String,
    private val service: SearchService
) : PagingSource<Int, UiUser>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UiUser> {
        return try {
            val page = params.key ?: 0
            val result = service.searchUsers(query, page = page, count = params.loadSize).map {
                it.toDbUser().toUi()
            }
            LoadResult.Page(data = result, prevKey = null, nextKey = page + 1)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}