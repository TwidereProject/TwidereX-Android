package com.twidere.twiderex.db.model

import androidx.room.*
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.UserKey


@Entity(
    tableName = "status",
    indices = [Index(value = ["statusId"], unique = true)],
)
data class DbStatus(
    /**
     * Id that being used in the database
     */
    @PrimaryKey
    val _id: String,
    /**
     * Actual tweet/toots id
     */
    val statusId: String,
    val userKey: UserKey,
    val platformType: PlatformType,
    val text: String,
    val timestamp: Long,
    val retweetCount: Long,
    val likeCount: Long,
    val replyCount: Long,
    val retweeted: Boolean,
    val liked: Boolean,
    val extra: String,
    val placeString: String?,
    val hasMedia: Boolean,
    @Embedded(prefix = "user_")
    val user: User,
)

data class User(
    val id: String,
    val name: String,
    val screenName: String,
    val profileImage: String,
)

data class DbStatusWithMedia(
    @Embedded
    val status: DbStatus,
    @Relation(parentColumn = "statusId", entityColumn = "statusId")
    val media: List<DbMedia>,
)