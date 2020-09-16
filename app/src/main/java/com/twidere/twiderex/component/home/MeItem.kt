package com.twidere.twiderex.component.home

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset

class MeItem : HomeNavigationItem() {
    override val name: String
        get() = "Me"
    override val icon: VectorAsset
        get() = Icons.Default.AccountCircle

    @Composable
    override fun onCompose() {
        Column {
            Text(text = "Me")
        }
    }
}