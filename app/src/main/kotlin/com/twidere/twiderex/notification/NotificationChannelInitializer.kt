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

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.startup.Initializer

class NotificationChannelInitializerHolder

class NotificationChannelInitializer : Initializer<NotificationChannelInitializerHolder> {
    override fun create(context: Context): NotificationChannelInitializerHolder {
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        val addedChannels = mutableListOf<String>()
        for (spec in NotificationChannelSpec.values()) {
            if (spec.grouped) continue
            val builder = NotificationChannelCompat.Builder(spec.id, spec.importance)
                .setName(context.getString(spec.nameRes))

            if (spec.descriptionRes != 0) {
                builder.setDescription(context.getString(spec.descriptionRes))
            }
            builder.setShowBadge(spec.showBadge)
            val channel = builder.build()
            notificationManagerCompat.createNotificationChannel(channel)
            addedChannels.add(channel.id)
        }

        notificationManagerCompat.notificationChannelsCompat.forEach {
            if (it.id !in addedChannels && it.group == null) {
                notificationManagerCompat.deleteNotificationChannel(it.id)
            }
        }
        return NotificationChannelInitializerHolder()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
