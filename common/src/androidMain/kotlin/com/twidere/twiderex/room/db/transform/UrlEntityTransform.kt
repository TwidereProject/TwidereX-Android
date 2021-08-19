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
package com.twidere.twiderex.room.db.transform

import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUrlEntity
import com.twidere.twiderex.room.db.model.DbUrlEntity
import java.util.UUID

fun DbUrlEntity.toUi() = UiUrlEntity(
    url = url,
    expandedUrl = expandedUrl,
    displayUrl = displayUrl,
    title = title,
    description = description,
    image = image
)
fun List<DbUrlEntity>.toUi() = map { it.toUi() }

fun List<UiUrlEntity>.toDbUrl(belongToKey: MicroBlogKey) = map {
    DbUrlEntity(
        url = it.url,
        _id = UUID.randomUUID().toString(),
        statusKey = belongToKey,
        expandedUrl = it.expandedUrl,
        displayUrl = it.displayUrl,
        title = it.title,
        description = it.description,
        image = it.image
    )
}
