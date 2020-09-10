package com.twidere.twiderex.component

import android.print.PrintDocumentAdapter
import android.util.Log
import android.webkit.WebChromeClient
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
    webViewClient: WebViewClient = WebViewClient(),
    webChromeClient: WebChromeClient = WebChromeClient(),
    webContext: WebContext = WebContext(),
    enableJavascript: Boolean = true,
    javascriptInterface: Map<String, Any> = mapOf()
) {
    if (WebContext.debug) {
        Log.d("WebComponent", "WebComponent compose $url")
    }
    AndroidView(::WebView) {
        it.webChromeClient = webChromeClient
        it.settings.javaScriptEnabled = enableJavascript
        it.webViewClient = webViewClient
        javascriptInterface.forEach { item ->
            it.addJavascriptInterface(item.value, item.key)
        }
        it.setRef { view -> webContext.webView = view }
        it.setUrl(url)
    }
}