/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MediaType
import java.net.URI

@Immutable
data class UiMedia(
    val url: String?,
    val belongToKey: MicroBlogKey,
    val mediaUrl: String?,
    val previewUrl: Any?,
    val type: MediaType,
    val width: Long = 0,
    val height: Long = 0,
    val pageUrl: String? = "",
    val altText: String = "",
    val order: Int = 0,
) {
    val fileName: String?
        get() = mediaUrl?.takeIfFileName() ?: url?.takeIfFileName() ?: findFileName()

    private fun findFileName(): String? {
        return mediaUrl?.let {
            val start = it.indexOf("?format=")
            val end = it.indexOf("&name=")
            val ext = it.substring(start + "?format=".length, end)
            it.lastPathSegment() + ".$ext"
        }
    }

    private fun String.takeIfFileName(): String? {
        return lastPathSegment().takeIf {
            it.contains(".")
        }
    }

    private fun String.lastPathSegment(): String {
        return try {
            URI.create(this).path.let {
                it.substring(it.lastIndexOf("/") + 1)
            }
        } catch (e: Throwable) {
            ""
        }
    }

    companion object {
        @Composable
        fun sample() = listOf(
            UiMedia(
                url = null,
                belongToKey = MicroBlogKey.Empty,
                mediaUrl = null,
                previewUrl = painterResource(res = com.twidere.twiderex.MR.files.ic_display_media_preview),
                type = MediaType.photo,
                width = 0,
                height = 0,
                pageUrl = null,
                altText = "",
                order = 0,
            ),
        )
    }
}
