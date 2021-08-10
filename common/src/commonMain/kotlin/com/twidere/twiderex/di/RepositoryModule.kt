package com.twidere.twiderex.di

import com.twidere.twiderex.dataprovider.DataProvider
import com.twidere.twiderex.repository.CacheRepository
import com.twidere.twiderex.repository.MediaRepository
import org.koin.dsl.module

internal val repositoryModules = module {
    factory { MediaRepository(get<DataProvider>().cacheDatabase.mediaDao()) }
    factory { CacheRepository(get()) }
}