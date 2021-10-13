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
package com.twidere.twiderex.kmp

import android.content.Context
import com.twidere.twiderex.di.ext.get
import java.io.File

actual class StorageProvider(private val context: Context) {
    // for persistence data
    actual val appDataDir: String get() = "${context.filesDir.absolutePath}/app".mkdirs()

    // for cache data
    actual val cacheDataDir: String get() = "${context.cacheDir.absolutePath}/data".mkdirs()

    // for media caches e.g image, video
    actual val mediaCacheDir: String get() = "${context.cacheDir.absolutePath}/media".mkdirs()

    actual fun clearCaches(dir: String, deleteDirAlso: Boolean) {
        val cacheDir = File(dir)
        if (!cacheDir.exists()) return
        if (deleteDirAlso)
            cacheDir.deleteRecursively()
        else {
            cacheDir.listFiles()?.forEach { file ->
                file.deleteRecursively()
            }
        }
    }

    actual companion object {
        actual fun create(): StorageProvider {
            return com.twidere.twiderex.kmp.StorageProvider(get())
        }
    }
}
