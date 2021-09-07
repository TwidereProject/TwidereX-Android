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

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.twidere.twiderex.model.enums.MediaType

data class UiMediaInsert(
    val uri: Uri,
    val type: MediaType
) {
    companion object {
        fun UiMediaInsert.getVideoThumb(context: Context): Bitmap? {
            return if (type == MediaType.video) {
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
            } else null
        }
    }
}
