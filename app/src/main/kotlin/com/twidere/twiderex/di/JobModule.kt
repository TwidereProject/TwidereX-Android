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
package com.twidere.twiderex.di

import android.content.Context
import com.twidere.twiderex.jobs.common.DownloadMediaJob
import com.twidere.twiderex.jobs.common.NotificationJob
import com.twidere.twiderex.jobs.common.ShareMediaJob
import com.twidere.twiderex.jobs.database.DeleteDbStatusJob
import com.twidere.twiderex.jobs.status.DeleteStatusJob
import com.twidere.twiderex.notification.AppNotificationManager
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.NotificationRepository
import com.twidere.twiderex.repository.StatusRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object JobModule {

    @Provides
    fun provideShareMediaJob(): ShareMediaJob = ShareMediaJob()

    @Provides
    fun provideDownloadMediaJob(
        accountRepository: AccountRepository,
        inAppNotification: InAppNotification
    ): DownloadMediaJob = DownloadMediaJob(
        accountRepository = accountRepository,
        inAppNotification = inAppNotification
    )

    @Provides
    fun provideDeleteDbStatusJob(
        statusRepository: StatusRepository
    ): DeleteDbStatusJob = DeleteDbStatusJob(
        statusRepository = statusRepository
    )

    @Provides
    fun provideDeleteStatusJob(
        accountRepository: AccountRepository,
        inAppNotification: InAppNotification,
        statusRepository: StatusRepository
    ): DeleteStatusJob = DeleteStatusJob(
        accountRepository = accountRepository,
        statusRepository = statusRepository,
        inAppNotification = inAppNotification
    )
    @Provides
    fun provideNotificationJob(
        @ApplicationContext context: Context,
        accountRepository: AccountRepository,
        repository: NotificationRepository,
        notificationManager: AppNotificationManager
    ): NotificationJob = NotificationJob(
        applicationContext = context,
        repository = repository,
        accountRepository = accountRepository,
        notificationManager = notificationManager
    )
}
