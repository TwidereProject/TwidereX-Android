package com.twidere.twiderex.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.ui.standardPadding


@IncomingComposeUpdate
fun <T> LazyListScope.itemsGridIndexed(
    data: List<T>,
    rowSize: Int,
    spacing: Dp = 0.dp,
    padding: Dp = 0.dp,
    itemContent: @Composable BoxScope.(Int, T) -> Unit,
) {
    item {
        Spacer(modifier = Modifier.height(standardPadding * 2))
    }
    val rows = data.windowed(rowSize, rowSize, true)
    itemsIndexed(rows) { index, row ->
        Column(
            modifier = Modifier.fillParentMaxWidth().padding(horizontal = padding)
        ) {
            Row {
                for (i in row.indices) {
                    val item = row[i]
                    Box(modifier = Modifier.weight(1f)) {
                        itemContent(data.indexOf(item), item)
                    }
                    if (i != row.size - 1) {
                        Spacer(modifier = Modifier.width(spacing))
                    }
                }
            }
            if (index != rows.size - 1) {
                Spacer(modifier = Modifier.height(spacing))
            }
        }
    }
    item {
        Spacer(modifier = Modifier.height(standardPadding * 2))
    }
}