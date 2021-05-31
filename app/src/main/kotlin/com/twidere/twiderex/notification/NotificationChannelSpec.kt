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

import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import com.twidere.twiderex.R

enum class NotificationChannelSpec(
    val id: String,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int = 0,
    val importance: Int,
    val showBadge: Boolean = false,
    val grouped: Boolean = false
) {
    /**
     * For notifications indicate that some lengthy operations are performing in the background.
     * Such as sending attachment process.
     */
    BackgroundProgresses(
        "background_progresses",
        R.string.common_notification_channel_background_progresses_name,
        importance = NotificationManagerCompat.IMPORTANCE_HIGH
    ),

    ContentInteractions(
        "content_interactions", R.string.common_notification_channel_content_interactions_name,
        descriptionRes = R.string.common_notification_channel_content_interactions_description,
        importance = NotificationManagerCompat.IMPORTANCE_HIGH, showBadge = true, grouped = true
    ),
}
