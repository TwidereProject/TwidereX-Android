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
package com.twidere.twiderex.scenes.mastodon

import android.net.Uri
import androidx.compose.runtime.Composable
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.WebComponent
import com.twidere.twiderex.extensions.DisposeResult
import com.twidere.twiderex.extensions.setResult
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene

val MASTODON_CALLBACK_URL = "https://org.mariotaku.twidere/auth/callback/mastodon"

@Composable
fun MastodonWebSignInScene(target: String) {
    val navController = LocalNavController.current
    TwidereScene {
        navController.DisposeResult(key = "code")
        InAppNotificationScaffold {
            WebComponent(
                url = target,
                onPageStarted = { _, url ->
                    if (url.startsWith(MASTODON_CALLBACK_URL)) {
                        val uri = Uri.parse(url)
                        uri.getQueryParameter("code")?.takeIf {
                            it.isNotEmpty()
                        }?.let {
                            navController.setResult("code", it)
                            navController.popBackStack()
                        }
                    }
                },
            )
        }
    }
}
