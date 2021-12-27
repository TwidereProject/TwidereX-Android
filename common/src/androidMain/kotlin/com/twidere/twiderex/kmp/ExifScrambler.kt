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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.util.UUID

actual class ExifScrambler(private val context: Context) {
    actual fun removeExifData(file: String, compress: Int): String {
        // first get input stream
        val uri = Uri.parse(file)
        val contentResolver = context.contentResolver
        contentResolver.openInputStream(uri)?.use { input ->
            // decode to bitmap because bitmap won't store exif meta data
            val bitmap = try {
                BitmapFactory.decodeStream(input)
            } catch (oom: OutOfMemoryError) {
                return file
            }
            println("exif ==> file:$uri, size:${(contentResolver.openFileDescriptor(uri, "r")?.statSize ?: 0) /(1024.0 * 1024)}， compress$compress")
            // create an cache image
            val mimeType = contentResolver.getType(uri) ?: ""
            val imageType = getImageType(mimeType)
            val imageCache = File(context.externalCacheDir, "${UUID.randomUUID()}.${imageType.name.lowercase()}")
            if (!imageCache.exists()) imageCache.createNewFile()
            // write to disk without exif meta data
            when (imageType) {
                ImageType.JPG -> {
                    val originExif = ExifInterface(input)
                    imageCache.outputStream().use {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, compress, it)
                        it.flush()
                    }
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
                        bitmap.compress(Bitmap.CompressFormat.PNG, compress, it)
                        it.flush()
                    }
                }
                ImageType.UNKNOWN -> {
                    return uri.toString()
                }
            }
            println("exif ==> imageCache:${imageCache.absolutePath}, size:${imageCache.length() /(1024.0 * 1024)}")
            return imageCache.toUri().toString()
        }
        return uri.toString()
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
