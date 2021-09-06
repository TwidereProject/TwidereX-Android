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
import coil.imageLoader
import coil.util.CoilUtils
import com.twidere.services.nitter.NitterService
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.CacheRepository
import com.twidere.twiderex.repository.DirectMessageRepository
import com.twidere.twiderex.repository.DraftRepository
import com.twidere.twiderex.repository.GifRepository
import com.twidere.twiderex.repository.ListsRepository
import com.twidere.twiderex.repository.ListsUsersRepository
import com.twidere.twiderex.repository.MediaRepository
import com.twidere.twiderex.repository.NotificationRepository
import com.twidere.twiderex.repository.ReactionRepository
import com.twidere.twiderex.repository.SearchRepository
import com.twidere.twiderex.repository.StatusRepository
import com.twidere.twiderex.repository.TimelineRepository
import com.twidere.twiderex.repository.TrendRepository
import com.twidere.twiderex.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideDraftRepository(database: AppDatabase): DraftRepository =
        DraftRepository(database = database)

    @Provides
    fun provideSearchRepository(database: AppDatabase): SearchRepository =
        SearchRepository(database = database)

    @Provides
    fun provideCacheRepository(
        database: CacheDatabase,
        appDatabase: AppDatabase,
        @ApplicationContext context: Context,
    ): CacheRepository = CacheRepository(
        database = database,
        appDatabase = appDatabase,
        cache = CoilUtils.createDefaultCache(context),
        imageLoader = context.imageLoader,
        cacheDirs = listOf(context.cacheDir, *context.externalCacheDirs),
    )

    @Provides
    fun provideStatusRepository(
        database: CacheDatabase,
        nitterService: NitterService?,
    ): StatusRepository = StatusRepository(
        database = database,
        nitterService = nitterService,
    )

    @Provides
    fun provideReactionRepository(database: CacheDatabase): ReactionRepository =
        ReactionRepository(database = database)

    @Provides
    fun provideTimelineRepository(database: CacheDatabase): TimelineRepository =
        TimelineRepository(database = database)

    @Provides
    fun provideUserRepository(
        database: CacheDatabase,
        accountRepository: AccountRepository
    ): UserRepository = UserRepository(database = database, accountRepository = accountRepository)

    @Provides
    fun provideListsRepository(
        database: CacheDatabase
    ) = ListsRepository(database = database)

    @Provides
    fun provideListUsersRepository() = ListsUsersRepository()

    @Provides
    fun provideNotificationRepository(
        database: CacheDatabase,
    ): NotificationRepository = NotificationRepository(database = database)

    @Provides
    fun provideTrendRepository(
        database: CacheDatabase
    ) = TrendRepository(database = database)

    @Provides
    fun provideDirectMessageRepository(
        database: CacheDatabase
    ) = DirectMessageRepository(database = database)

    @Provides
    fun provideDirectMediaRepository(
        database: CacheDatabase
    ) = MediaRepository(database = database)

    @Provides
    fun provideGifRepository() = GifRepository()
}
