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

import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

actual class ExifScrambler {
    actual fun deleteCacheFile(file: String) {
        File(file).takeIf { it.exists() && it.isFile }?.delete()
    }

    actual fun removeExifData(file: String, maxImageSize: Long): String {
        return try {
            val imgFile = File(file)
            val image = ImageIO.read(imgFile)
            val compressed = ByteArrayOutputStream()
            val outputStream = ImageIO.createImageOutputStream(compressed)
            val writer = ImageIO.getImageWritersByFormatName("JPEG").next()
            val writeParam: ImageWriteParam = writer.defaultWriteParam
            writeParam.compressionMode = ImageWriteParam.MODE_EXPLICIT
            writeParam.compressionQuality = 1f
            writer.output = outputStream
            do {
                compressed.flush()
                compressed.reset()
                writer.write(null, IIOImage(image, null, null), writeParam)
                writeParam.compressionQuality -= 0.05f
            } while (compressed.toByteArray().size > maxImageSize && writeParam.compressionQuality > 0.7f)
            writer.dispose()
            val result = File.createTempFile(imgFile.name, null).apply {
                outputStream().use {
                    it.write(compressed.toByteArray())
                    it.flush()
                }
            }
            result.absolutePath
        } catch (e: Exception) {
            // not an image or compression failed
            file
        }
    }
}
