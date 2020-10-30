package com.twidere.twiderex.scenes.twitter

import android.webkit.JavascriptInterface
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.twidere.twiderex.component.WebComponent
import com.twidere.twiderex.ui.AmbientNavController

private const val INJECT_CONTENT =
    "javascript:window.injector.tryPinCode(document.querySelector('#oauth_pin code').textContent);"

@Composable
fun TwitterWebSignInScene(target: String) {
    WebComponent(
        url = target,
        onPageFinished = { view, _ ->
            view.loadUrl(INJECT_CONTENT)
        },
        javascriptInterface = mapOf("injector" to TwitterWebJavascriptInterface(AmbientNavController.current))
    )
}

private class TwitterWebJavascriptInterface(
    val navController: NavController,
) {
    @JavascriptInterface
    fun tryPinCode(content: String?) {
        if (!content.isNullOrEmpty()) {
            content.toIntOrNull()?.let {
                //TODO:
//                setFragmentResult("request_pin_code", bundleOf("pin_code" to content))
                navController.popBackStack()
            }
        }
    }
}