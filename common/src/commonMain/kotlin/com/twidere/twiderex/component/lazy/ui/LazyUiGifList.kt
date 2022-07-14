/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.component.lazy.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.twidere.twiderex.component.foundation.GifTag
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.lazy.itemsPagingGridIndexed
import com.twidere.twiderex.component.lazy.loadState
import com.twidere.twiderex.model.ui.UiGif

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LazyUiGifList(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<UiGif>,
    selectedItem: UiGif?,
    state: LazyListState = rememberLazyListState(),
    onItemSelected: (UiGif) -> Unit = {},
    header: LazyListScope.() -> Unit = {},
) {
    LazyUiList(items = items) {
        LazyColumn(
            modifier = modifier,
            state = state,
        ) {
            header.invoke(this)
            itemsPagingGridIndexed(
                data = items,
                rowSize = 2,
                spacing = LazyUiGifListDefaults.Spacing
            ) { _, item ->
                item?.let {
                    val selected = selectedItem?.url == it.url
                    Box(
                        modifier = Modifier.clip(MaterialTheme.shapes.medium)
                            .clickable { onItemSelected(item) }
                            .then(
                                if (selected) Modifier.border(
                                    LazyUiGifListDefaults.Border, color = MaterialTheme.colors.primary,
                                    shape = MaterialTheme.shapes.medium
                                ) else Modifier
                            )
                    ) {
                        NetworkImage(
                            data = it.preview,
                            Modifier.fillMaxWidth()
                                .aspectRatio(1f)
                        )
                        GifTag(Modifier.align(Alignment.BottomEnd))
                    }
                }
            }
            loadState(items.loadState.append) {
                items.retry()
            }
        }
    }
}

private object LazyUiGifListDefaults {
    object Tag {
        val Width = 25.dp
        val Height = 16.dp
        val Padding = PaddingValues(end = 8.dp, bottom = 8.dp)
    }
    val Spacing = 16.dp
    val Border = 3.dp
}
