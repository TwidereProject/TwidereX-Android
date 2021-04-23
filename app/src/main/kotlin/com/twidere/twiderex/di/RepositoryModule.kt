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
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.CacheRepository
import com.twidere.twiderex.repository.DraftRepository
import com.twidere.twiderex.repository.ListsRepository
import com.twidere.twiderex.repository.ReactionRepository
import com.twidere.twiderex.repository.SearchRepository
import com.twidere.twiderex.repository.StatusRepository
import com.twidere.twiderex.repository.TimelineRepository
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
    fun provideStatusRepository(database: CacheDatabase): StatusRepository =
        StatusRepository(database = database)

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
}
