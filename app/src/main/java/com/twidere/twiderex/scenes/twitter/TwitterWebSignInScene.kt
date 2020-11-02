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
package com.twidere.twiderex.scenes.twitter

import android.webkit.JavascriptInterface
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.twidere.twiderex.component.WebComponent
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val INJECT_CONTENT =
    "javascript:window.injector.tryPinCode(document.querySelector('#oauth_pin code').textContent);"

@Composable
fun TwitterWebSignInScene(target: String) {
    TwidereXTheme {
        WebComponent(
            url = target,
            onPageFinished = { view, _ ->
                view.loadUrl(INJECT_CONTENT)
            },
            javascriptInterface = mapOf(
                "injector" to TwitterWebJavascriptInterface(
                    AmbientNavController.current
                )
            )
        )
    }
}

private class TwitterWebJavascriptInterface(
    val navController: NavController,
) {
    @JavascriptInterface
    fun tryPinCode(content: String?) {
        if (!content.isNullOrEmpty()) {
            content.toIntOrNull()?.let {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "pin_code",
                            content
                        )
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}
