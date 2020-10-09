package com.twidere.twiderex.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(
    tableName = "user",
    indices = [Index(value = ["userId"], unique = true)],
)
@JsonClass(generateAdapter = true)
data class DbUser(
    /**
     * Id that being used in the database
     */
    @PrimaryKey
    var _id: String,
    val userId: String,
    val name: String,
    val screenName: String,
    val profileImage: String,
    val profileBackgroundImage: String?,
    val followersCount: Long,
    val friendsCount: Long,
    val listedCount: Long,
    val desc: String,
    val website: String?,
    val location: String?,
    val verified: Boolean,
    val isProtected: Boolean,
)