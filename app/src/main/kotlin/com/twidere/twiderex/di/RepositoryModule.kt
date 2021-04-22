package com.twidere.twiderex.di

import android.content.Context
import com.bumptech.glide.Glide
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.CacheRepository
import com.twidere.twiderex.repository.DraftRepository
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
        glide = Glide.get(context),
        cacheDirs = listOf(context.cacheDir, *context.externalCacheDirs)
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
}