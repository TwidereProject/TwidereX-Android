package com.twidere.twiderex.fragment.twitter

import android.webkit.JavascriptInterface
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.twidere.twiderex.component.WebComponent
import com.twidere.twiderex.fragment.ComposeFragment

private const val INJECT_CONTENT =
    "javascript:window.injector.tryPinCode(document.querySelector('#oauth_pin code').textContent);"

class TwitterWebSignInFragment : ComposeFragment() {

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