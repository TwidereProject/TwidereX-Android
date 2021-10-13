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

val StorageProvider.downloadDir get() = "$cacheDataDir/download".mkdirs()
val StorageProvider.dataStoreDir get() = "$appDataDir/datastore".mkdirs()
val StorageProvider.appDatabaseDir get() = "$appDataDir/database".mkdirs()
val StorageProvider.cacheDatabaseDir get() = "$cacheDataDir/database".mkdirs()

fun StorageProvider.appDatabasePath(name: String) = "$appDatabaseDir/$name"
fun StorageProvider.cacheDatabasePath(name: String) = "$cacheDatabaseDir/$name"
fun StorageProvider.dataStorePath(name: String) = "$dataStoreDir/$name"
fun StorageProvider.downloadFilePath(name: String) = "$downloadDir/$name"
fun StorageProvider.mediaCacheFilePath(name: String) = "$mediaCacheDir/$name"

fun String.mkdirs(): String {
    File(this).apply {
        if (!exists()) mkdirs()
    }
    return this
}

fun String.file(createIfNotExists: Boolean = true): File {
    return File(this).apply {
        if (createIfNotExists && !exists()) createNewFile()
    }
}

fun String.mkFile(): String {
    file()
    return this
}
