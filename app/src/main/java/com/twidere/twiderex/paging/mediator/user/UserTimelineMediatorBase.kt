package com.twidere.twiderex.paging.mediator.user

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import coil.network.HttpException
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.model.DbUserTimelineWithStatus
import com.twidere.twiderex.db.model.saveToDb
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
abstract class UserTimelineMediatorBase(
    private val database: AppDatabase,
) : RemoteMediator<Int, DbUserTimelineWithStatus>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DbUserTimelineWithStatus>
    ): MediatorResult {
        try {
            val key = when (loadType) {
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    lastItem.status.status.data.statusId
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.REFRESH -> {
                    null
                }
            }
            val pageSize = state.config.pageSize

            val result = load(pageSize, key)

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    clearData(database)
                }
                result.saveToDb(database)
            }

            return MediatorResult.Success(
                endOfPaginationReached = result.size < pageSize
            )
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    abstract suspend fun clearData(database: AppDatabase)

    protected abstract suspend fun load(
        pageSize: Int,
        max_id: String?
    ): List<DbUserTimelineWithStatus>
}