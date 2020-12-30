/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.utils

import android.content.Context
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.twidere.twiderex.R
import java.io.File

class CacheDataSourceFactory(
    private val context: Context,
    private val maxFileSize: Long,
) : DataSource.Factory {
    private val simpleCache: SimpleCache by lazy {
        VideoCache.getInstance(context)
    }

    private val defaultDatasourceFactory: DefaultDataSourceFactory
    override fun createDataSource(): DataSource {
        return CacheDataSource(
            simpleCache, defaultDatasourceFactory.createDataSource(),
            FileDataSource(), CacheDataSink(simpleCache, maxFileSize),
            CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null
        )
    }

    init {
        val userAgent = Util.getUserAgent(context, context.getString(R.string.app_name))
        val bandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
        defaultDatasourceFactory = DefaultDataSourceFactory(
            this.context,
            bandwidthMeter,
            DefaultHttpDataSourceFactory(userAgent, bandwidthMeter)
        )
    }
}

object VideoCache {
    private var simpleCache: SimpleCache? = null
    private const val maxCacheSize: Long = 100 * 1024 * 1024
    fun getInstance(context: Context): SimpleCache {
        val evictor = LeastRecentlyUsedCacheEvictor(maxCacheSize)
        if (simpleCache == null) simpleCache =
            SimpleCache(File(context.cacheDir, "media"), evictor, ExoDatabaseProvider(context))
        return simpleCache as SimpleCache
    }
}
