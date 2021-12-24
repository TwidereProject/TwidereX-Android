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
package com.twidere.twiderex.utils

import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.StringWriter
import java.lang.Exception

object WindowsRegistry {

    fun registryUrlProtocol(protocol: String) {
        val root = File("${System.getProperty("user.home")}/.TwidereX")
        if (!root.exists()) {
            root.mkdirs()
        }
        val regFile = File("${root.absolutePath}/deeplink.reg")
        if (!regFile.exists()) {
            regFile.createNewFile()
            val reg = """
        Windows Registry Editor Version 5.00

        [HKEY_CLASSES_ROOT\TwidereX]
        "URL Protocol"="$protocol"
        @="Twidere X"

        [HKEY_CLASSES_ROOT\TwidereX\shell]

        [HKEY_CLASSES_ROOT\TwidereX\shell\open]

        [HKEY_CLASSES_ROOT\TwidereX\shell\open\command]
        @="\"${File("").absolutePath.replace("\\", "\\\\")}\\Twidere X.exe\" \"%1\""
            """.trimIndent()
            regFile.writeText(reg)
        }
        try {
            val process = ProcessBuilder("cmd", "/c", "regedit", "/s", regFile.canonicalPath).start()
            process.waitFor()
        } catch (e: Throwable) {
            try {
                // if process is not working use Desktop open file
                Desktop.getDesktop().open(regFile)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun readRegistry(location: String, key: String): String? {
        return try {
            // Run reg query, then read output with StreamReader (internal class)
            val process = ProcessBuilder("reg", "query", location, "/v", key).start()
            val reader = StreamReader(process.inputStream)
            reader.start()
            process.waitFor()
            reader.join()
            reader.result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    internal class StreamReader(private val input: InputStream) : Thread() {
        private val sw: StringWriter = StringWriter()
        override fun run() {
            try {
                var c: Int
                while ((input.read().also { c = it }) != -1) sw.write(c)
            } catch (e: IOException) {
            }
        }

        val result: String
            get() = sw.toString()
    }
}
