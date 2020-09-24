package com.twidere.twiderex.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.twidere.twiderex.db.model.DbTimeline
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.model.UserKey

@Dao
interface TimelineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(timeline: List<DbTimeline>)

    @Transaction
    @Query("SELECT * FROM timeline WHERE userKey == :userKey AND type == :timelineType ORDER BY timestamp DESC")
    fun getAllWithLiveData(
        userKey: UserKey,
        timelineType: TimelineType
    ): LiveData<List<DbTimelineWithStatus>>

    @Query("SELECT * FROM timeline WHERE statusId == :id")
    suspend fun findWithId(id: String): DbTimeline?

    @Update
    suspend fun update(vararg timeline: DbTimeline)
}