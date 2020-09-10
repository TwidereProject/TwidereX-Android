package com.twidere.twiderex.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.compose(content: @Composable () -> Unit): ComposeView {
    return ComposeView(requireContext()).apply {
        setContent(content)
    }
}

inline fun <reified T> Fragment.getNavigationResult(key: String) =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

inline fun <reified T> Fragment.setNavigationResult(key: String, result: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}
