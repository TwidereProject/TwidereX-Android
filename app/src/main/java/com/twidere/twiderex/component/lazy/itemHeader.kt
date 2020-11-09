package com.twidere.twiderex.component.lazy

import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

fun LazyListScope.itemHeader(title: @Composable () -> Unit) {
    item {
        ListItem {
            ProvideTextStyle(value = MaterialTheme.typography.button) {
                title.invoke()
            }
        }
    }
}