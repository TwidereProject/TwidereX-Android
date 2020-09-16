package com.twidere.twiderex.component.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset


abstract class HomeNavigationItem {
    abstract val name: String
    abstract val icon: VectorAsset

    @Composable
    abstract fun onCompose()
}