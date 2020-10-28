package com.twidere.twiderex.component.settings

import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

fun <T> LazyListScope.radioItem(
    title: @Composable () -> Unit,
    items: List<T>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    itemContent: @Composable (item: T) -> Unit,
) {
    item {
        ProvideTextStyle(value = MaterialTheme.typography.button) {
            title.invoke()
        }
    }

    itemsIndexed(items) { index, item ->
        ListItem(
            modifier = Modifier.clickable(onClick = { onSelected(index) }),
            text = {
                itemContent.invoke(item)
            },
            trailing = {
                RadioButton(selected = index == selectedIndex, onClick = { onSelected(index) })
            }
        )
    }
}