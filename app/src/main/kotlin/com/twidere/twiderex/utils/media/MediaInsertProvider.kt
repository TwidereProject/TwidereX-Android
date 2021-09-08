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
package com.twidere.twiderex.utils.media

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.ui.UiMediaInsert
import kotlinx.coroutines.coroutineScope

// Todo change to expect/actual after merge to desktop
class MediaInsertProvider(private val context: Context) {

    suspend fun provideUiMediaInsert(uri: Uri): UiMediaInsert {
        val type = (context.contentResolver.getType(uri) ?: "image/*").let {
            when {
                it.startsWith("video") -> MediaType.video
                it == "image/gif" -> MediaType.animated_gif
                else -> MediaType.photo
            }
        }
        return UiMediaInsert(
            uri = uri,
            preview = if (type == MediaType.video) getVideoThumbnail(uri) ?: uri else uri,
            type = type,
        )
    }

    private suspend fun getVideoThumbnail(uri: Uri): Bitmap? {
        return coroutineScope {
            var bitmap: Bitmap? = null
            var mediaMetadataRetriever: MediaMetadataRetriever? = null
            try {
                mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(context, uri)
                bitmap = mediaMetadataRetriever.getFrameAtTime(
                    1000,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mediaMetadataRetriever?.release()
            }
            bitmap
        }
    }
}
