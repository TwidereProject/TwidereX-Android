package com.twidere.twiderex.di

import com.twidere.twiderex.dataprovider.DataProvider
import org.koin.dsl.module

internal val twidereModules = module {
    single { DataProvider.create() }
    single { get<DataProvider>().appCacheHandler }
    single { get<DataProvider>().cacheDatabase }
    single { get<DataProvider>().appDatabase }
}