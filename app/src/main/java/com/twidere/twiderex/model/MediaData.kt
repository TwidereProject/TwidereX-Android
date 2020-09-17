package com.twidere.twiderex.model

data class MediaData(
    val previewUrl: String?,
    val sourceUrl: String?,
    val pageUrl: String?,
    val type: MediaType,
)

enum class MediaType {
    photo,
    video,
    animated_gif,
}