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
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageInputStream

actual class FileResolver {
    actual fun getMimeType(file: String): String? {
        return File(file).takeIf { it.exists() && it.isFile }?.let {
            Files.probeContentType(it.toPath())
        }
    }

    actual fun getFileSize(file: String): Long? {
        return File(file).takeIf { it.exists() && it.isFile }?.length()
    }

    actual fun openInputStream(file: String): InputStream? {
        return File(file).takeIf { it.exists() && it.isFile }?.inputStream()
    }

    actual fun openOutputStream(file: String): OutputStream? {
        return File(file).apply {
            if (!exists()) {
                createNewFile()
            }
        }.takeIf { it.exists() && it.isFile }?.outputStream()
    }

    actual fun getMediaSize(file: String): MediaSize {
        val imgFile = File(file)
        val iter = ImageIO.getImageReadersBySuffix(imgFile.extension)
        while (iter.hasNext()) {
            val reader = iter.next()
            try {
                val stream = FileImageInputStream(imgFile)
                reader.input = stream
                val width = reader.getWidth(reader.minIndex)
                val height = reader.getHeight(reader.minIndex)
                return MediaSize(width.toLong(), height.toLong())
            } catch (e: IOException) {
            } finally {
                reader.dispose()
            }
        }
        throw IOException("Not a known image file: " + imgFile.absolutePath)
    }
}
