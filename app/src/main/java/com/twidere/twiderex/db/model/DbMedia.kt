package com.twidere.twiderex.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.twidere.twiderex.model.MediaType

@Entity(
    tableName = "media",
    indices = [Index(value = ["statusId", "mediaUrl"], unique = true)],
)
data class DbMedia(
    /**
     * Id that being used in the database
     */
    @PrimaryKey
    val _id: String,
    val statusId: String,
    val url: String?,
    val mediaUrl: String?,
    val previewUrl: String?,
    val type: MediaType,
    val width: Long,
    val height: Long,
    val pageUrl: String?,
    val altText: String,
    val order: Int,
)
