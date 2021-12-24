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
package com.twidere.twiderex.db.sqldelight.transform

import com.twidere.twiderex.model.ui.UiDraft
import com.twidere.twiderex.sqldelight.table.Draft

fun Draft.toUi() = UiDraft(
    draftId = id,
    content = content,
    media = media,
    createdAt = createAt,
    composeType = composeType,
    statusKey = statusKey,
    excludedReplyUserIds = excludedReplyUserIds
)

fun UiDraft.toDbDraft() = Draft(
    id = draftId,
    content = content,
    media = media,
    createAt = createdAt,
    composeType = composeType,
    statusKey = statusKey,
    excludedReplyUserIds = excludedReplyUserIds
)
