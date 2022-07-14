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
package com.twidere.twiderex.scenes.twitter

import androidx.compose.runtime.Composable
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.WebComponent
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.utils.TwitterWebJavascriptInterface

const val INJECT_CONTENT =
    "javascript:window.injector.tryPinCode(document.querySelector('#oauth_pin code').textContent);"

@Composable
fun TwitterWebSignInScene(target: String) {
    val navController = LocalNavController.current
    TwidereScene {
        InAppNotificationScaffold {
            WebComponent(
                url = target,
                onPageFinished = { view, _ ->
                    view.loadUrl(INJECT_CONTENT)
                },
                javascriptInterface = mapOf(
                    "injector" to TwitterWebJavascriptInterface(
                        navController
                    )
                ),
            )
        }
    }
}
