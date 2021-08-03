/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.model.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.painterResource
import com.twidere.twiderex.R
import com.twidere.twiderex.db.model.DbMedia
import com.twidere.twiderex.model.enums.MediaType

@Immutable
data class UiMedia(
    val url: String?,
    val mediaUrl: String?,
    val previewUrl: Any?,
    val type: MediaType,
    val width: Long,
    val height: Long,
    val pageUrl: String?,
    val altText: String,
) {
    val fileName: String?
        get() = mediaUrl?.takeIfFileName() ?: url?.takeIfFileName() ?: findFileName()

    private fun findFileName(): String? {
        return mediaUrl?.let {
            val start = it.indexOf("?format=")
            val end = it.indexOf("&name=")
            val ext = it.substring(start + "?format=".length, end)
            Uri.parse(it).lastPathSegment + ".$ext"
        }
    }

    private fun String.takeIfFileName(): String? {
        return let {
            Uri.parse(it)
        }?.lastPathSegment?.takeIf {
            it.contains(".")
        }
    }

    companion object {
        @Composable
        fun sample() = listOf(
            UiMedia(
                url = null,
                mediaUrl = null,
                previewUrl = painterResource(id = R.drawable.featured_graphics),
                type = MediaType.photo,
                width = painterResource(id = R.drawable.featured_graphics).intrinsicSize.width.toLong(),
                height = painterResource(id = R.drawable.featured_graphics).intrinsicSize.height.toLong(),
                pageUrl = null,
                altText = "",
            ),
        )

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
