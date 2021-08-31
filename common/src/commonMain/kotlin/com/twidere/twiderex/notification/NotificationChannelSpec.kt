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
package com.twidere.twiderex.notification

import android.net.Uri
import com.twidere.twiderex.model.MicroBlogKey

enum class NotificationChannelSpec(
    val id: String,
    val showBadge: Boolean = false,
    val grouped: Boolean = false
) {
    /**
     * For notifications indicate that some lengthy operations are performing in the background.
     * Such as sending attachment process.
     */
    BackgroundProgresses(
        "background_progresses",
    ),

    ContentInteractions(
        "content_interactions",
        showBadge = true,
        grouped = true
    ),

    ContentMessages(
        "content_messages",
        showBadge = true,
        grouped = true
    )
}

fun MicroBlogKey.notificationChannelId(id: String): String {
    return "${id}_${Uri.encode(toString())}"
}

fun MicroBlogKey.notificationChannelGroupId(): String {
    return Uri.encode(toString())
}
