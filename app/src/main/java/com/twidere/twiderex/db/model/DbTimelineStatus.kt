package com.twidere.twiderex.db.model

import androidx.room.*
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.UserKey

@Entity(
    tableName = "timeline",
    indices = [Index(value = ["statusId"], unique = true)],
)
data class DbTimeline(
    @PrimaryKey
    val _id: String,
    val userKey: UserKey,
    val platformType: PlatformType,
    val timestamp: Long,
    var isGap: Boolean,
    val statusId: String,
    val statusDbId: String,
    val retweetDbId: String?,
    val quoteDbId: String?,
)

data class DbTimelineWithStatus(
    @Embedded
    val timeline: DbTimeline,

    @Relation(parentColumn = "statusDbId", entityColumn = "_id")
    val status: DbStatus,
    @Relation(parentColumn = "retweetDbId", entityColumn = "_id")
    val retweet: DbStatus?,
    @Relation(parentColumn = "quoteDbId", entityColumn = "_id")
    val quote: DbStatus?,
)