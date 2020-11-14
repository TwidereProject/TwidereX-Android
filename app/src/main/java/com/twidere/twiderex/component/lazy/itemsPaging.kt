package com.twidere.twiderex.component.lazy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.twidere.twiderex.component.foundation.loading


fun <T : Any> LazyListScope.itemsPaging(
    lazyPagingItems: LazyPagingItems<T>,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    loadState(lazyPagingItems.loadState.refresh) {
        lazyPagingItems.retry()
    }
    items(lazyPagingItems, itemContent)
    loadState(lazyPagingItems.loadState.append) {
        lazyPagingItems.retry()
    }
}

fun LazyListScope.loadState(
    state: LoadState,
    onReloadRequested: () -> Unit = {},
) {
    when (state) {
        is LoadState.Loading -> loading()
        is LoadState.Error -> item {
            item {
                ListItem(
                    modifier = Modifier.clickable(onClick = { onReloadRequested.invoke() }),
                    text = {
                        Text(text = "Error")
                    }
                )
            }
        }
        else -> {

        }
    }
}