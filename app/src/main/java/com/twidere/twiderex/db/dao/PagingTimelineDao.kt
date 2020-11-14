package com.twidere.twiderex.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.twidere.twiderex.db.model.DbPagingTimeline
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.model.UserKey

@Dao
interface PagingTimelineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(timeline: List<DbPagingTimeline>)

    @Query("SELECT * FROM paging_timeline WHERE statusId == :id AND userKey == :userKey")
    suspend fun findWithStatusId(id: String, userKey: UserKey): DbPagingTimeline?

    @Transaction
    @Query("SELECT * FROM paging_timeline WHERE pagingKey == :pagingKey AND userKey == :userKey ORDER BY timestamp DESC")
    fun getPagingSource(
        pagingKey: String,
        userKey: UserKey,
    ): PagingSource<Int, DbPagingTimelineWithStatus>


    @Query("DELETE FROM paging_timeline WHERE pagingKey == :pagingKey AND userKey == :userKey")
    suspend fun clearAll(
        pagingKey: String,
        userKey: UserKey,
    )
}