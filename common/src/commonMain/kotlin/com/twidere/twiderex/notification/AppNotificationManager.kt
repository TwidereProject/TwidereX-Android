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

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

expect class AppNotificationManager {
    fun notify(notificationId: Int, appNotification: AppNotification)

    @OptIn(ExperimentalTime::class)
    fun notifyTransient(
        notificationId: Int,
        appNotification: AppNotification,
        duration: Duration = 5.seconds,
    )
}

class AppNotification(
    val channelId: String,
    val title: CharSequence? = null,
    val content: CharSequence? = null,
    val largeIcon: String? = null,
    val deepLink: String? = null,
    val ongoing: Boolean = false,
    val progress: Int = 0,
    val progressMax: Int = 0,
    val progressIndeterminate: Boolean = false,
    val silent: Boolean = false,
) {
    class Builder(private var channelId: String) {
        private var title: CharSequence? = null
        private var content: CharSequence? = null
        private var largeIcon: String? = null
        private var deepLink: String? = null
        private var ongoing: Boolean = false
        private var progress: Int = 0
        private var progressMax: Int = 0
        private var progressIndeterminate: Boolean = false
        private var silent: Boolean = false

        fun setContentTitle(title: CharSequence?) = this.apply {
            this.title = title
        }

        fun setContentText(content: CharSequence?) = this.apply {
            this.content = content
        }

        fun setLargeIcon(largeIcon: String?) = this.apply {
            this.largeIcon = largeIcon
        }

        fun setDeepLink(deepLink: String?) = this.apply {
            this.deepLink = deepLink
        }

        fun setOngoing(ongoing: Boolean) = this.apply {
            this.ongoing = ongoing
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
            ongoing = ongoing,
            progress = progress,
            progressMax = progressMax,
            progressIndeterminate = progressIndeterminate,
            silent = silent
        )
    }
}
