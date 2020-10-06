package com.twidere.twiderex.extensions

import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

val WindowAmbient = ambientOf<Window> { error("No Window") }
val NavControllerAmbient = ambientOf<NavController> { error("No NavController") }
fun Fragment.compose(content: @Composable () -> Unit): ComposeView {
    return ComposeView(requireContext()).apply {
        setContent {
            Providers(
                NavControllerAmbient provides findNavController(),
                WindowAmbient provides activity?.window!!,
            ) {
                content.invoke()
            }
        }
    }
}
