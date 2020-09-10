package com.twidere.twiderex.fragment

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

class HomeFragment : ComposeFragment() {

    @Composable
    override fun onCompose() {
        Column {
            Text(text = "Home")
        }
    }
}