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
package com.twidere.twiderex.kmp

import java.io.File

actual class StorageProvider() {
    private val rootDir = "TwidereX"
    // for persistence data
    actual val appDir: String get() = "${getWorkDirectory()}/$rootDir/app".mkdirs()

    // for cache data
    actual val cacheDir: String get() = "${getWorkDirectory()}/$rootDir/cache".mkdirs()

    // for media caches e.g image, video
    actual val mediaCacheDir: String get() = "${getWorkDirectory()}/$rootDir/mediaCaches".mkdirs()

    actual fun clearCaches(dir: String, deleteDirAlso: Boolean) {
        val cacheDir = File(dir)
        if (!cacheDir.exists()) return
        if (deleteDirAlso)
            cacheDir.deleteRecursively()
        else {
            cacheDir.listFiles()?.forEach { file ->
                // delete database may cause error
                if (!file.absolutePath.contains(cacheFiles.databaseDir)) file.deleteRecursively()
            }
        }
    }

    private fun getWorkDirectory(): String {
        var workingDirectory: String?
        // here, we assign the name of the OS, according to Java, to a variable...
        val os = (System.getProperty("os.name"))?.uppercase() ?: throw Error("Can't get os name for this device!")
        // to determine what the workingDirectory is.
        // if it is some version of Windows
        if (os.contains("WIN")) {
            // it is simply the location of the "AppData" folder
            workingDirectory = System.getenv("AppData")
        }
        // Otherwise, we assume Linux or Mac
        else {
            // in either case, we would start in the user's home directory
            workingDirectory = System.getProperty("user.home")
            // if we are on a Mac, we are not done, we look for "Application Support"
            if (os.contains("MAC"))
                workingDirectory += "/Library/Application Support"
        }
        return workingDirectory ?: throw Error("Can't get work directory for os:$os!")
    }

    actual companion object {
        actual fun create(): StorageProvider {
            return com.twidere.twiderex.kmp.StorageProvider()
        }
    }
}
