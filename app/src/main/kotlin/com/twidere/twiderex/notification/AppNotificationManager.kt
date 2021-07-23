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

import android.graphics.Bitmap
import java.util.concurrent.TimeUnit

interface AppNotificationManager {
    fun notify(notificationId: Int, appNotification: AppNotification)

    fun notifyTransient(notificationId: Int, appNotification: AppNotification, duration: Long = 5, durationTimeUnit: TimeUnit = TimeUnit.SECONDS)
}

open class AppNotification(
    val channelId: String,
    val title: String,
    val content: CharSequence? = null,
    val largeIcon: Bitmap? = null,
    val deepLink: String? = null,
    val onGoing: Boolean = false,
    val progress: Int = 0,
    val progressMax: Int = 0,
    val progressIndeterminate: Boolean = false,
    val silent: Boolean = false,
) {
    class Builder(private var channelId: String, private var title: String) {
        private var content: CharSequence? = null
        private var largeIcon: Bitmap? = null
        private var deepLink: String? = null
        private var onGoing: Boolean = false
        private var progress: Int = 0
        private var progressMax: Int = 0
        private var progressIndeterminate: Boolean = false
        private var silent: Boolean = false

        fun setContent(content: CharSequence) = this.apply {
            this.content = content
        }

        fun setLargeIcon(largeIcon: Bitmap?) = this.apply {
            this.largeIcon = largeIcon
        }

        fun setDeepLink(deepLink: String?) = this.apply {
            this.deepLink = deepLink
        }

        fun setOnGoing(onGoing: Boolean) = this.apply {
            this.onGoing = onGoing
        }

        fun setSilent(silent: Boolean) = this.apply {
            this.silent = silent
        }

        fun setProgress(max: Int, progress: Int, indeterminate: Boolean) = this.apply {
            this.progress = progress
            this.progressMax = max
            this.progressIndeterminate = indeterminate
        }

        fun build() = AppNotification(
            title = title,
            channelId = channelId,
            content = content,
            largeIcon = largeIcon,
            deepLink = deepLink,
            onGoing = onGoing,
            progress = progress,
            progressMax = progressMax,
            progressIndeterminate = progressIndeterminate,
            silent = silent
        )
    }
}
