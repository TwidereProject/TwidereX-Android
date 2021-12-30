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

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.twidere.twiderex.di.ext.get
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

actual class ExifScrambler(private val context: Context) {

    actual fun removeExifData(file: String, maxImageSize: Long): String {
        val uri = Uri.parse(file)
        val contentResolver = context.contentResolver
        try {
            contentResolver.openInputStream(uri)?.use { input ->
                // create an cache image
                val mimeType = contentResolver.getType(uri) ?: ""
                val imageType = getImageType(mimeType)
                val imageCache = File(get<StorageProvider>().appFiles.mediaDir, "${UUID.randomUUID()}.${imageType.name.lowercase()}")
                if (!imageCache.exists()) imageCache.createNewFile()
                // write to disk without exif meta data
                when (imageType) {
                    ImageType.JPG -> {
                        imageCache.outputStream().use {
                            compressImage(contentResolver, uri, maxImageSize, it)
                        }
                        val originExif = ExifInterface(input)
                        // keep origin images orientation
                        originExif.getAttribute(ExifInterface.TAG_ORIENTATION)?.let {
                            ExifInterface(imageCache.absolutePath).apply {
                                setAttribute(ExifInterface.TAG_ORIENTATION, it)
                                saveAttributes()
                            }
                        }
                    }
                    ImageType.PNG -> {
                        imageCache.outputStream().use {
                            compressImage(contentResolver, uri, maxImageSize, it)
                        }
                    }
                    ImageType.UNKNOWN -> {
                        return uri.toString()
                    }
                }
                return imageCache.toUri().toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uri.toString()
    }

    private fun compressImage(contentResolver: ContentResolver, uri: Uri, maxImageSize: Long, fos: FileOutputStream) {
        contentResolver.openInputStream(uri)?.use {
            val bitmap = try {
                BitmapFactory.decodeStream(it)
            } catch (oom: OutOfMemoryError) {
                throw oom
            }
            var currSize: Int
            var currQuality = 100
            val stream = ByteArrayOutputStream()
            do {
                stream.flush()
                stream.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, currQuality, stream)
                currSize = stream.toByteArray().size
                currQuality -= 5
            } while (currSize >= maxImageSize && currQuality >= 80)
            stream.toByteArray()
        }?.apply {
            fos.write(this)
            fos.flush()
        } ?: throw Error("Failed to open input stream")
    }

    actual fun deleteCacheFile(file: String) {
        Uri.parse(file).path?.let {
            File(it)
        }?.apply {
            if (exists()) delete()
        }
    }

    private fun getImageType(mimeType: String): ImageType {
        return when (mimeType) {
            "image/jpeg" -> {
                ImageType.JPG
            }
            "image/png", "image/x-png", "image/webp", "image-x-webp" -> {
                ImageType.PNG
            }
            else -> {
                ImageType.UNKNOWN
            }
        }
    }
}

enum class ImageType {
    JPG,
    PNG,
    UNKNOWN
}
