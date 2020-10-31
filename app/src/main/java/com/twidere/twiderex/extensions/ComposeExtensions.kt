package com.twidere.twiderex.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.twidere.twiderex.ui.AmbientViewModelProviderFactory

@Composable
inline fun <reified VM : ViewModel> navViewModel(
    key: String? = null,
    factory: ViewModelProvider.Factory? = AmbientViewModelProviderFactory.current,
): VM = viewModel(key, factory)