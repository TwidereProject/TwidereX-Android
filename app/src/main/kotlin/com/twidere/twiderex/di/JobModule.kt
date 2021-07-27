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
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.jobs.common.DownloadMediaJob
import com.twidere.twiderex.jobs.common.NotificationJob
import com.twidere.twiderex.jobs.common.ShareMediaJob
import com.twidere.twiderex.jobs.compose.MastodonComposeJob
import com.twidere.twiderex.jobs.compose.TwitterComposeJob
import com.twidere.twiderex.jobs.database.DeleteDbStatusJob
import com.twidere.twiderex.jobs.dm.DirectMessageDeleteJob
import com.twidere.twiderex.jobs.dm.DirectMessageFetchJob
import com.twidere.twiderex.jobs.dm.TwitterDirectMessageSendJob
import com.twidere.twiderex.jobs.draft.RemoveDraftJob
import com.twidere.twiderex.jobs.draft.SaveDraftJob
import com.twidere.twiderex.jobs.status.DeleteStatusJob
import com.twidere.twiderex.jobs.status.LikeStatusJob
import com.twidere.twiderex.jobs.status.MastodonVoteJob
import com.twidere.twiderex.jobs.status.RetweetStatusJob
import com.twidere.twiderex.jobs.status.UnRetweetStatusJob
import com.twidere.twiderex.jobs.status.UnlikeStatusJob
import com.twidere.twiderex.kmp.ExifScrambler
import com.twidere.twiderex.kmp.FileResolver
import com.twidere.twiderex.kmp.RemoteNavigator
import com.twidere.twiderex.notification.AppNotificationManager
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.DirectMessageRepository
import com.twidere.twiderex.repository.DraftRepository
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

    @Provides
    fun provideLikeStatusJob(
        accountRepository: AccountRepository,
        statusRepository: StatusRepository,
        inAppNotification: InAppNotification
    ): LikeStatusJob = LikeStatusJob(
        accountRepository = accountRepository,
        statusRepository = statusRepository,
        inAppNotification = inAppNotification
    )

    @Provides
    fun provideRetweetStatusJob(
        accountRepository: AccountRepository,
        statusRepository: StatusRepository,
        inAppNotification: InAppNotification
    ): RetweetStatusJob = RetweetStatusJob(
        accountRepository = accountRepository,
        statusRepository = statusRepository,
        inAppNotification = inAppNotification
    )

    @Provides
    fun provideUnlikeStatusJob(
        accountRepository: AccountRepository,
        statusRepository: StatusRepository,
        inAppNotification: InAppNotification
    ): UnlikeStatusJob = UnlikeStatusJob(
        accountRepository = accountRepository,
        statusRepository = statusRepository,
        inAppNotification = inAppNotification
    )

    @Provides
    fun provideUnRetweetStatusJob(
        accountRepository: AccountRepository,
        statusRepository: StatusRepository,
        inAppNotification: InAppNotification
    ): UnRetweetStatusJob = UnRetweetStatusJob(
        accountRepository = accountRepository,
        statusRepository = statusRepository,
        inAppNotification = inAppNotification
    )

    @Provides
    fun provideMastodonVoteJob(
        accountRepository: AccountRepository,
        statusRepository: StatusRepository,
        inAppNotification: InAppNotification
    ): MastodonVoteJob = MastodonVoteJob(
        accountRepository = accountRepository,
        statusRepository = statusRepository,
        inAppNotification = inAppNotification
    )

    @Provides
    fun provideRemoveDraftJob(
        repository: DraftRepository
    ): RemoveDraftJob = RemoveDraftJob(
        repository = repository,
    )

    @Provides
    fun provideSaveDraftJob(
        repository: DraftRepository,
        inAppNotification: InAppNotification
    ): SaveDraftJob = SaveDraftJob(
        repository = repository,
        inAppNotification = inAppNotification
    )

    @Provides
    fun provideDirectMessageDeleteJob(
        repository: DirectMessageRepository,
        accountRepository: AccountRepository
    ): DirectMessageDeleteJob = DirectMessageDeleteJob(
        repository = repository,
        accountRepository = accountRepository
    )

    @Provides
    fun provideDirectMessageFetchJob(
        @ApplicationContext context: Context,
        repository: DirectMessageRepository,
        accountRepository: AccountRepository,
        notificationManager: AppNotificationManager,
    ): DirectMessageFetchJob = DirectMessageFetchJob(
        applicationContext = context,
        repository = repository,
        accountRepository = accountRepository,
        notificationManager = notificationManager
    )

    @Provides
    fun provideTwitterDirectMessageSendJob(
        @ApplicationContext context: Context,
        accountRepository: AccountRepository,
        notificationManager: AppNotificationManager,
        fileResolver: FileResolver,
        cacheDatabase: CacheDatabase,
    ): TwitterDirectMessageSendJob = TwitterDirectMessageSendJob(
        context = context,
        accountRepository = accountRepository,
        notificationManager = notificationManager,
        fileResolver = fileResolver,
        cacheDatabase = cacheDatabase
    )

    @Provides
    fun provideTwitterComposeJob(
        @ApplicationContext context: Context,
        accountRepository: AccountRepository,
        notificationManager: AppNotificationManager,
        fileResolver: FileResolver,
        cacheDatabase: CacheDatabase,
        exifScrambler: ExifScrambler,
        statusRepository: StatusRepository,
        remoteNavigator: RemoteNavigator
    ): TwitterComposeJob = TwitterComposeJob(
        context = context,
        accountRepository = accountRepository,
        notificationManager = notificationManager,
        fileResolver = fileResolver,
        cacheDatabase = cacheDatabase,
        exifScrambler = exifScrambler,
        statusRepository = statusRepository,
        remoteNavigator = remoteNavigator
    )

    @Provides
    fun provideMastodonComposeJob(
        @ApplicationContext context: Context,
        accountRepository: AccountRepository,
        notificationManager: AppNotificationManager,
        fileResolver: FileResolver,
        cacheDatabase: CacheDatabase,
        exifScrambler: ExifScrambler,
        remoteNavigator: RemoteNavigator
    ): MastodonComposeJob = MastodonComposeJob(
        context = context,
        accountRepository = accountRepository,
        notificationManager = notificationManager,
        fileResolver = fileResolver,
        cacheDatabase = cacheDatabase,
        exifScrambler = exifScrambler,
        remoteNavigator = remoteNavigator
    )
}
