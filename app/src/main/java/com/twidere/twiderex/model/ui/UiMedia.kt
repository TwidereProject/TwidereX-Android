package com.twidere.twiderex.model.ui

import android.os.Parcelable
import com.twidere.twiderex.db.model.DbMedia
import com.twidere.twiderex.model.MediaType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UiMedia(
    val url: String?,
    val mediaUrl: String?,
    val previewUrl: String?,
    val type: MediaType,
    val width: Long,
    val height: Long,
    val pageUrl: String?,
    val altText: String,
) : Parcelable {
    companion object {
        fun List<DbMedia>.toUi() = sortedBy { it.order }.map {
            UiMedia(
                url = it.url,
                mediaUrl = it.mediaUrl,
                previewUrl = it.previewUrl,
                type = it.type,
                width = it.width,
                height = it.height,
                pageUrl = it.pageUrl,
                altText = it.altText,
            )
        }
    }
}