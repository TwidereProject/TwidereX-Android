/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
 
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
