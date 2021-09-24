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
package com.twidere.twiderex.db.sqldelight.transform

import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUrlEntity
import com.twidere.twiderex.sqldelight.table.UrlEntity
import java.util.UUID

fun UiUrlEntity.toDbUrlEntity(belongToKey: MicroBlogKey, id: String = UUID.randomUUID().toString()) = UrlEntity(
    id = id,
    url = url,
    belongToKey = belongToKey,
    expandedUrl = expandedUrl,
    displayUrl = displayUrl,
    title = title,
    description = description,
    image = image
)

fun UrlEntity.toUi() = UiUrlEntity(
    url = url,
    expandedUrl = expandedUrl,
    displayUrl = displayUrl,
    title = title,
    description = description,
    image = image
)
