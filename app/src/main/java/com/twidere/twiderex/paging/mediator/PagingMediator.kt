package com.twidere.twiderex.paging.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.RemoteMediator
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.UserKey

@OptIn(ExperimentalPagingApi::class)
abstract class PagingMediator(
    val database: AppDatabase,
    val userKey: UserKey,
): RemoteMediator<Int, DbPagingTimelineWithStatus>() {
    abstract val pagingKey: String
}

fun PagingMediator.pager(
    pageSize: Int = defaultLoadCount,
): Pager<Int, DbPagingTimelineWithStatus> {
    return Pager(config = PagingConfig(pageSize = pageSize), remoteMediator = this) {
        database.pagingTimelineDao().getPagingSource(pagingKey = pagingKey, userKey = userKey)
    }
}