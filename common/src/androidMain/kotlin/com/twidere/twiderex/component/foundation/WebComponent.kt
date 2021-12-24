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
package com.twidere.twiderex.component.foundation

import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.twidere.twiderex.view.LollipopFixWebView

class WebContext {
    fun goForward() {
        webView?.goForward()
    }

    fun goBack() {
        webView?.goBack()
    }

    fun canGoBack(): Boolean {
        return webView?.canGoBack() ?: false
    }

    internal var webView: WebView? = null
}

private fun WebView.setRef(ref: (WebView) -> Unit) {
    ref(this)
}

private fun WebView.setUrl(url: String) {
    if (originalUrl != url) {
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
    config: (WebView) -> Unit = {},
) {
    var progress by remember { mutableStateOf(0f) }
    if (webContext.canGoBack()) {
        BackHandler {
            webContext.goBack()
        }
    }
    Box {
        if (progress != 1f) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .zIndex(1f),
                progress = progress
            )
        }
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                LollipopFixWebView(context).also {
                    it.settings.apply {
                        javaScriptEnabled = enableJavascript
                    }
                    it.webChromeClient = ComposeWebChromeClient(
                        onProgress = {
                            progress = it
                        }
                    )
                    it.webViewClient = ComposeWebViewClient(onPageFinished, onPageStarted)
                    javascriptInterface.forEach { item ->
                        it.addJavascriptInterface(item.value, item.key)
                    }
                    it.setRef { view -> webContext.webView = view }
                    config.invoke(it)
                    it.setUrl(url)
                }
            },
        )
    }
}

private class ComposeWebChromeClient(
    private val onProgress: (Float) -> Unit = {}
) : WebChromeClient() {
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        onProgress.invoke(newProgress.toFloat() / 100f)
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
