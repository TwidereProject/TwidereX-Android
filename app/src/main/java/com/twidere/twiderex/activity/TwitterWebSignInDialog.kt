/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.activity

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import com.twidere.twiderex.TwidereXActivity
import com.twidere.twiderex.scenes.twitter.INJECT_CONTENT
import com.twidere.twiderex.utils.TwitterWebJavascriptInterface
import com.twidere.twiderex.view.LollipopFixWebView

class TwitterWebSignInDialog : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Black_NoTitleBar_Fullscreen
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                requireActivity().let {
                    it as TwidereXActivity
                }.navController.let {
                    it.previousBackStackEntry?.savedStateHandle?.set(
                        "pin_code",
                        "",
                    )
                    it.popBackStack()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LollipopFixWebView(requireContext()).also { webView ->
            webView.settings.apply {
                javaScriptEnabled = true
            }

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    view?.loadUrl(INJECT_CONTENT)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                }
            }

            webView.addJavascriptInterface(
                TwitterWebJavascriptInterface(
                    requireActivity().let {
                        it as TwidereXActivity
                    }.navController
                ),
                "injector",
            )
            arguments?.getString("target")?.let {
                webView.loadUrl(it)
            }
        }
    }
}
