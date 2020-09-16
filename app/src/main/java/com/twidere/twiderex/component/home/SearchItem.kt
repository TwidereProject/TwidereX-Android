package com.twidere.twiderex.component.home

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset

class SearchItem : HomeNavigationItem() {
    override val name: String
        get() = "Search"
    override val icon: VectorAsset
        get() = Icons.Default.Search

    @Composable
    override fun onCompose() {
        Column {
            Text(text = "Search")
        }
    }
}