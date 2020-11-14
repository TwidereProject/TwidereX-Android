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
    tableName = "paging_timeline",
    indices = [Index(
        value = ["userKey", "statusId", "pagingKey"],
        unique = true
    )],
)
data class DbPagingTimeline(
    @PrimaryKey
    val _id: String,
    val userKey: UserKey,
    val pagingKey: String,
    val statusId: String,
    val timestamp: Long,
) {
    companion object {
        fun DbTimelineWithStatus.toPagingDbTimeline(
            pagingKey: String
        ): DbPagingTimelineWithStatus {
            return DbPagingTimelineWithStatus(
                timeline = with(timeline) {
                    DbPagingTimeline(
                        _id = UUID.randomUUID().toString(),
                        userKey = userKey,
                        pagingKey = pagingKey,
                        timestamp = timestamp,
                        statusId = statusId,
                    )
                },
                status = status,
            )
        }
    }
}

data class DbPagingTimelineWithStatus(
    @Embedded
    val timeline: DbPagingTimeline,

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

fun UserTimelineType.pagingKey(screenName: String) = "user:$screenName:$this"

suspend fun List<DbPagingTimelineWithStatus>.saveToDb(
    database: AppDatabase,
) {
    val data = this
        .map { listOf(it.status.status, it.status.quote, it.status.retweet) }
        .flatten()
        .filterNotNull()
    data.saveToDb(database)
    this.map { it.timeline }.let {
        database.pagingTimelineDao().insertAll(it)
    }
}
