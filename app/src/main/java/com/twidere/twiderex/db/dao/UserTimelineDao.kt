package com.twidere.twiderex.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.twidere.twiderex.db.model.DbUserTimeline
import com.twidere.twiderex.db.model.DbUserTimelineWithStatus
import com.twidere.twiderex.db.model.UserTimelineType
import com.twidere.twiderex.model.UserKey

@Dao
interface UserTimelineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(timeline: List<DbUserTimeline>)

    @Transaction
    @Query("SELECT * FROM user_timeline WHERE screenName == :screenName AND userKey == :userKey AND type == :timelineType ORDER BY timestamp DESC")
    fun getPagingSource(
        screenName: String,
        timelineType: UserTimelineType,
        userKey: UserKey,
    ): PagingSource<Int, DbUserTimelineWithStatus>


    @Query("DELETE FROM user_timeline WHERE screenName == :screenName AND userKey == :userKey AND type == :timelineType")
    suspend fun clearAll(
        screenName: String,
        timelineType: UserTimelineType,
        userKey: UserKey,
    )
}