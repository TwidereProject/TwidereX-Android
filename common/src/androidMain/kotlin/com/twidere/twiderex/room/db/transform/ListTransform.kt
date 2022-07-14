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

import com.twidere.twiderex.model.ui.UiList
import com.twidere.twiderex.room.db.model.DbList
import java.util.UUID

internal fun DbList.toUi() =
    UiList(
        id = listId,
        ownerId = ownerId,
        listKey = listKey,
        accountKey = accountKey,
        title = title,
        descriptions = description,
        mode = mode,
        replyPolicy = replyPolicy,
        isFollowed = isFollowed,
        allowToSubscribe = allowToSubscribe,
    )

internal fun UiList.toDbList(dbId: String = UUID.randomUUID().toString()) =
    DbList(
        listId = id,
        ownerId = ownerId,
        listKey = listKey,
        accountKey = accountKey,
        title = title,
        description = descriptions,
        mode = mode,
        replyPolicy = replyPolicy,
        isFollowed = isFollowed,
        allowToSubscribe = allowToSubscribe,
        _id = dbId
    )
