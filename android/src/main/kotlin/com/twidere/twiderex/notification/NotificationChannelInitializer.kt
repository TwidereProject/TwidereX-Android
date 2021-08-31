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
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationManagerCompat
import androidx.startup.Initializer
import com.twidere.twiderex.di.InitializerEntryPoint
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.repository.AccountRepository
import javax.inject.Inject

class NotificationChannelInitializerHolder

class NotificationChannelInitializer : Initializer<NotificationChannelInitializerHolder> {
    @Inject
    lateinit var repository: AccountRepository

    override fun create(context: Context): NotificationChannelInitializerHolder {
        InitializerEntryPoint.resolve(context).inject(this)

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        val addedChannels = mutableListOf<String>()
        for (spec in NotificationChannelSpec.values().filter { !it.grouped }) {
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

        updateAccountChannelsAndGroups(context)

        return NotificationChannelInitializerHolder()
    }

    private fun updateAccountChannelsAndGroups(context: Context) {
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        val accounts = repository.getAccounts()
        val specs = NotificationChannelSpec.values().filter { it.grouped }

        val addedChannels = mutableListOf<String>()
        val addedGroups = mutableListOf<String>()

        accounts.forEach { account ->
            val group = NotificationChannelGroupCompat
                .Builder(account.accountKey.notificationChannelGroupId())
                .setName(account.account.name.let { MicroBlogKey.valueOf(it) }.id)
                .setDescription(account.account.name)
                .build()
            addedGroups.add(group.id)
            notificationManagerCompat.createNotificationChannelGroup(group)

            for (spec in specs) {
                val channel = NotificationChannelCompat
                    .Builder(account.accountKey.notificationChannelId(spec.id), spec.importance)
                    .setName(context.getString(spec.nameRes))
                    .let {
                        if (spec.descriptionRes != 0) {
                            it.setDescription(context.getString(spec.descriptionRes))
                        } else {
                            it
                        }
                    }
                    .setGroup(group.id)
                    .setShowBadge(spec.showBadge)
                    .build()

                notificationManagerCompat.createNotificationChannel(channel)
                addedChannels.add(channel.id)
            }
        }

        // Delete all channels and groups of non-existing accounts
        notificationManagerCompat.notificationChannelsCompat.forEach {
            if (it.id !in addedChannels && it.group != null) {
                notificationManagerCompat.deleteNotificationChannel(it.id)
            }
        }
        notificationManagerCompat.notificationChannelGroupsCompat.forEach {
            if (it.id !in addedGroups) {
                notificationManagerCompat.deleteNotificationChannelGroup(it.id)
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
