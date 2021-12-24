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

import com.twidere.services.mastodon.model.Emoji
import com.twidere.twiderex.model.ui.UiEmoji
import com.twidere.twiderex.model.ui.UiEmojiCategory

internal fun List<Emoji>.toUi(): List<UiEmojiCategory> = groupBy({ it.category }, { it }).map {
    UiEmojiCategory(
        if (it.key.isNullOrEmpty()) null else it.key,
        it.value.map { emoji ->
            UiEmoji(
                shortcode = emoji.shortcode,
                url = emoji.url,
                staticURL = emoji.staticURL,
                visibleInPicker = emoji.visibleInPicker,
                category = emoji.category
            )
        }
    )
}
