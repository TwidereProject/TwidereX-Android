package com.twidere.twiderex.fragment.twitter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.twidere.twiderex.component.WebComponent
import com.twidere.twiderex.extensions.compose

private const val INJECT_CONTENT =
    "javascript:window.injector.tryPinCode(document.querySelector('#oauth_pin code').textContent);"

class TwitterWebSignInFragment : Fragment() {

    private val args by navArgs<TwitterWebSignInFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return compose {
            WebComponent(
                url = args.target,
                onPageFinished = { view, url ->
                    view.loadUrl(INJECT_CONTENT)
                },
                javascriptInterface = mapOf("injector" to this)
            )
        }
    }

    @JavascriptInterface
    fun tryPinCode(content: String?) {
        if (!content.isNullOrEmpty()) {
            content.toIntOrNull()?.let {
                setFragmentResult("request_pin_code", bundleOf("pin_code" to content))
                findNavController().popBackStack()
            }
        }
    }
}