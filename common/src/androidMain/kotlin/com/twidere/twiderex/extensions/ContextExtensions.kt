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
package com.twidere.twiderex.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

fun Context.checkAllSelfPermissionsGranted(vararg permissions: String): Boolean {
    return permissions.none { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
}

fun Context.checkAnySelfPermissionsGranted(vararg permissions: String): Boolean {
    return permissions.any { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
}

fun Context.shareText(content: String) {
    startActivity(
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }.let {
            Intent.createChooser(it, null).apply {
                if (this@shareText !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    )
}

fun Context.shareMedia(uri: Uri, mimeType: String) {
    startActivity(
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = mimeType
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }.let {
            Intent.createChooser(it, null).apply {
                if (this@shareMedia !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    )
}

fun Context.launchAppSetting() {
    startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts(
                "package",
                packageName,
                null
            )
        )
    )
}
