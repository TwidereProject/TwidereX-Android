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

import java.io.File

internal val StorageProvider.downloadDir get() = "$cacheDataDir/share".mkdirs()
internal val StorageProvider.dataStoreDir get() = "$appDataDir/datastore".mkdirs()
internal val StorageProvider.appDatabaseDir get() = "$appDataDir/database".mkdirs()
internal val StorageProvider.cacheDatabaseDir get() = "$cacheDataDir/database".mkdirs()
internal fun StorageProvider.appDatabasePath(name: String) = "$appDatabaseDir/$name"
internal fun StorageProvider.cacheDatabasePath(name: String) = "$cacheDatabaseDir/$name"
internal fun StorageProvider.dataStorePath(name: String) = "$dataStoreDir/$name"
internal fun StorageProvider.downloadFilePath(name: String) = "$downloadDir/$name"

internal fun String.mkdirs(): String {
    File(this).apply {
        if (!exists()) mkdirs()
    }
    return this
}

internal fun String.mkFile(): String {
    File(this).apply {
        if (!exists()) createNewFile()
    }
    return this
}
