package com.twidere.twiderex.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.model.UserKey
import java.util.*


@Entity(
    tableName = "user_timeline",
    indices = [Index(
        value = ["screenName", "userKey", "statusId", "type"],
        unique = true
    )],
)
data class DbUserTimeline(
    @PrimaryKey
    val _id: String,
    val userKey: UserKey,
    val screenName: String,
    val type: UserTimelineType,
    val statusId: String,
    val timestamp: Long,
) {
    companion object {
        fun DbTimelineWithStatus.toUserDbTimeline(
            screenName: String,
            type: UserTimelineType,
        ): DbUserTimelineWithStatus {
            return DbUserTimelineWithStatus(
                timeline = with(timeline) {
                    DbUserTimeline(
                        _id = UUID.randomUUID().toString(),
                        userKey = userKey,
                        screenName = screenName,
                        timestamp = timestamp,
                        type = type,
                        statusId = statusId,
                    )
                },
                status = status,
            )
        }
    }
}

data class DbUserTimelineWithStatus(
    @Embedded
    val timeline: DbUserTimeline,

    @Relation(
        parentColumn = "statusId",
        entityColumn = "statusId",
        entity = DbStatusV2::class,
    )
    val status: DbStatusWithReference,
)

enum class UserTimelineType {
    Status,
    Media,
    Favourite
}

suspend fun List<DbUserTimelineWithStatus>.saveToDb(
    database: AppDatabase,
) {
    val data = this
        .map { listOf(it.status.status, it.status.quote, it.status.retweet) }
        .flatten()
        .filterNotNull()
    data.saveToDb(database)
    this.map { it.timeline }.let {
        database.userTimelineDao().insertAll(it)
    }
}
