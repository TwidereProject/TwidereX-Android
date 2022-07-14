/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
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
package com.twidere.twiderex.image

import java.io.File
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest

internal interface ImageCache {
    suspend fun store(url: String, inputStream: InputStream): File?
    suspend fun fetch(url: String): File?
}

internal class ImageCacheImpl private constructor(
    private val cacheDir: String,
    private var maxCacheSize: Int, // unit MB
    private var cacheClearRate: Float // 0.1 - 1f, e.g. set rate to 0.2 will remove 20% caches when exceed max cache size
) : ImageCache {
    companion object Factory {
        private val caches = mutableMapOf<String, ImageCacheImpl>()
        fun create(cacheDir: String, maxCacheSize: Int = 200, cacheClearRate: Float = 0.25f): ImageCacheImpl {
            return caches[cacheDir]?.apply {
                this.maxCacheSize = maxCacheSize
                this.cacheClearRate = cacheClearRate
            } ?: ImageCacheImpl(cacheDir, maxCacheSize, cacheClearRate).also {
                caches[cacheDir] = it
            }
        }
    }

    private var currentSize = -1L

    override suspend fun store(url: String, inputStream: InputStream): File? {
        val cache = File(cacheDir, md5(url))
        return try {
            if (cache.exists()) cache.delete()
            checkCacheSize()
            inputStream.use { input ->
                cache.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            currentSize += cache.length()
            cache
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun fetch(url: String): File? {
        val cache = File(cacheDir, md5(url))
        if (!cache.exists()) return null
        return cache
    }

    private fun checkCacheSize() {
        if (currentSize == -1L) {
            currentSize = getFolderSize(File(cacheDir))
        }
        if (currentSize.mb() > maxCacheSize) cleanCacheSize()
    }

    private fun cleanCacheSize() {
        File(cacheDir).listFiles()?.let {
            for (file in it) {
                val size = file.length()
                if (file.delete()) currentSize -= size
                if (currentSize.mb() <= maxCacheSize * (1 - cacheClearRate)) break
            }
        }
    }

    private fun getFolderSize(directory: File): Long {
        var length: Long = 0
        if (directory.isDirectory) {
            directory.listFiles()?.let {
                for (file in it) {
                    length += if (file.isFile) file.length() else getFolderSize(file)
                }
            }
        } else {
            length = directory.length()
        }
        return length
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    private fun Long.mb() = (this / (1024 * 1024F))
}
