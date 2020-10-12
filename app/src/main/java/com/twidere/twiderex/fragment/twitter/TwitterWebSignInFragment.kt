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
package com.twidere.twiderex.fragment.twitter

import android.webkit.JavascriptInterface
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.twidere.twiderex.component.WebComponent
import com.twidere.twiderex.fragment.JetFragment

private const val INJECT_CONTENT =
    "javascript:window.injector.tryPinCode(document.querySelector('#oauth_pin code').textContent);"

class TwitterWebSignInFragment : JetFragment() {

    private val args by navArgs<TwitterWebSignInFragmentArgs>()

    @JavascriptInterface
    fun tryPinCode(content: String?) {
        if (!content.isNullOrEmpty()) {
            content.toIntOrNull()?.let {
                setFragmentResult("request_pin_code", bundleOf("pin_code" to content))
                findNavController().popBackStack()
            }
        }
    }

    @Composable
    override fun onCompose() {
        WebComponent(
            url = args.target,
            onPageFinished = { view, url ->
                view.loadUrl(INJECT_CONTENT)
            },
            javascriptInterface = mapOf("injector" to this)
        )
    }
}
