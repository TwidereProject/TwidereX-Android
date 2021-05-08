package com.twidere.twiderex.paging.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.twidere.services.microblog.model.IPaging
import com.twidere.services.microblog.model.IUser
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import com.twidere.twiderex.paging.crud.MemoryCachePagingSource
import com.twidere.twiderex.paging.crud.PagingMemoryCache

@ExperimentalPagingApi
abstract class UserPagingMediator(
    protected val userKey: MicroBlogKey,
    protected val memoryCache: PagingMemoryCache<String, UiUser>,
): RemoteMediator<String, UiUser>() {
    private var paging:String? = null
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<String, UiUser>
    ): MediatorResult {
        return try {
            paging = when(loadType) {
                LoadType.APPEND -> paging
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            }
            val result = loadUsers(paging, state.config.pageSize)
            val users = result.map {
                it.toDbUser(userKey).toUi()
            }
            // clear cache if refresh
            if (paging == null) {
                memoryCache.clear()
            }

            paging = if (result is IPaging && users.isNotEmpty()) {
                result.nextPage
            } else {
                null
            }
            // save users to cache
            memoryCache.insert(users)
            MediatorResult.Success(paging == null)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    abstract suspend fun loadUsers(key: String?, count: Int): List<IUser>
}

class UserPagingSource(memoryCache: PagingMemoryCache<String, UiUser>): MemoryCachePagingSource<String, UiUser>(memoryCache) {
    override fun getRefreshKey(state: PagingState<String, UiUser>): String? {
        return null
    }

    override fun provideNextKey(paging: Int): String? {
        return paging.toString()
    }
}