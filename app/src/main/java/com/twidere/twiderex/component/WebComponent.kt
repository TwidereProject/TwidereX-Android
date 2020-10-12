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
package com.twidere.twiderex.component

import android.graphics.Bitmap
import android.print.PrintDocumentAdapter
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.twidere.twiderex.BuildConfig

class WebContext {
    companion object {
        val debug = BuildConfig.DEBUG
    }

    fun createPrintDocumentAdapter(documentName: String): PrintDocumentAdapter {
        return webView.createPrintDocumentAdapter(documentName)
    }

    fun goForward() {
        webView.goForward()
    }

    fun goBack() {
        webView.goBack()
    }

    fun canGoBack(): Boolean {
        return webView.canGoBack()
    }

    internal lateinit var webView: WebView
}

private fun WebView.setRef(ref: (WebView) -> Unit) {
    ref(this)
}

private fun WebView.setUrl(url: String) {
    if (originalUrl != url) {
        if (WebContext.debug) {
            Log.d("WebComponent", "WebComponent load url")
        }
        loadUrl(url)
    }
}

@Composable
fun WebComponent(
    url: String,
    webContext: WebContext = WebContext(),
    enableJavascript: Boolean = true,
    javascriptInterface: Map<String, Any> = mapOf(),
    onPageFinished: ((view: WebView, url: String) -> Unit)? = null,
    onPageStarted: ((view: WebView, url: String) -> Unit)? = null,
) {
    if (WebContext.debug) {
        Log.d("WebComponent", "WebComponent compose $url")
    }
    AndroidView(::WebView) {
        it.settings.javaScriptEnabled = enableJavascript
        it.webViewClient = ComposeWebViewClient(onPageFinished, onPageStarted)
        javascriptInterface.forEach { item ->
            it.addJavascriptInterface(item.value, item.key)
        }
        it.setRef { view -> webContext.webView = view }
        it.setUrl(url)
    }
}

private class ComposeWebViewClient(
    val pageFinished: ((view: WebView, url: String) -> Unit)? = null,
    val pageStarted: ((view: WebView, url: String) -> Unit)? = null,
) : WebViewClient() {

    override fun onPageFinished(view: WebView, url: String) {
        pageFinished?.invoke(view, url)
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        pageStarted?.invoke(view, url)
    }
}
