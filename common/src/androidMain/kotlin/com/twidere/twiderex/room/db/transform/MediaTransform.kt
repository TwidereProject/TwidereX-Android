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
package com.twidere.twiderex.room.db.transform

import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.room.db.model.DbMedia
import java.util.UUID

internal fun List<DbMedia>.toUi() = sortedBy { it.order }.map {
    UiMedia(
        url = it.url,
        belongToKey = it.belongToKey,
        mediaUrl = it.mediaUrl,
        previewUrl = it.previewUrl,
        type = it.type,
        width = it.width,
        height = it.height,
        pageUrl = it.pageUrl,
        altText = it.altText,
        order = it.order,
    )
}

internal fun List<UiMedia>.toDbMedia() = map {
    DbMedia(
        url = it.url,
        belongToKey = it.belongToKey,
        mediaUrl = it.mediaUrl,
        previewUrl = it.previewUrl?.toString(),
        type = it.type,
        width = it.width,
        height = it.height,
        pageUrl = it.pageUrl,
        altText = it.altText,
        order = it.order,
        _id = UUID.randomUUID().toString(),
    )
}
