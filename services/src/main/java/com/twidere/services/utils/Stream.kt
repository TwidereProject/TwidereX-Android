/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.services.utils

import java.io.InputStream
import java.io.OutputStream

fun InputStream.copyToInLength(output: OutputStream, length: Int) {
    val buffer = ByteArray(1024)
    var bytesRead = 0
    do {
        val read = read(buffer)
        if (read == -1) {
            break
        }
        output.write(buffer, 0, read)
        bytesRead += read
    } while (bytesRead <= length)
}
