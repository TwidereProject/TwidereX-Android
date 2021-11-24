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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import com.twidere.twiderex.extensions.shareMedia
import com.twidere.twiderex.extensions.shareText

actual class RemoteNavigator(private val context: Context) {
    actual fun openDeepLink(deeplink: String, fromBackground: Boolean) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(deeplink).run {
                    scheme?.toLowerCase(
                        Locale.current
                    )?.let {
                        buildUpon().apply {
                            scheme(it)
                        }.build()
                    } ?: this
                }
            ).apply {
                if (this !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    actual fun shareMedia(filePath: String, mimeType: String, fromBackground: Boolean) {
        context.shareMedia(
            uri = Uri.parse(filePath),
            mimeType = mimeType,
        )
    }

    actual fun shareText(content: String, fromBackground: Boolean) {
        context.shareText(
            content = content,
        )
    }

    actual fun launchOAuthUri(uri: String) {
        CustomTabsIntent.Builder()
            .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
            .build().run {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                launchUrl(context, Uri.parse(uri))
            }
    }
}
