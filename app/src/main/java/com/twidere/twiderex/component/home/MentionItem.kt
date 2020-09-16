package com.twidere.twiderex.component.home

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset

class MentionItem : HomeNavigationItem() {
    override val name: String
        get() = "Mentions"
    override val icon: VectorAsset
        get() = Icons.Default.AlternateEmail

    @Composable
    override fun onCompose() {
        Column {
            Text(text = "Mentions")
        }
    }
}