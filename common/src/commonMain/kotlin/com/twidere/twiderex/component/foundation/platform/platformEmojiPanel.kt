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
package com.twidere.twiderex.component.foundation.platform

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.lazy.itemsGridIndexed
import com.twidere.twiderex.model.ui.UiEmoji
import com.twidere.twiderex.model.ui.UiEmojiCategory

@Composable
expect fun PlatformEmojiPanel(
    items: List<UiEmojiCategory>,
    showEmoji: Boolean,
    onEmojiSelected: (UiEmoji) -> Unit,
)

@ExperimentalFoundationApi
@Composable
internal fun EmojiList(
    items: List<UiEmojiCategory>,
    onEmojiSelected: (UiEmoji) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.padding(EmojiListDefaults.ContentPadding)) {
        val column = maxOf((maxWidth / EmojiListDefaults.Icon.Size).toInt(), 1)
        LazyColumn {
            items.forEach {
                it.category?.let { category ->
                    item {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(EmojiListDefaults.Category.ContentPadding)
                        )
                    }
                }
                itemsGridIndexed(
                    data = it.emoji,
                    rowSize = column,
                ) { _, item ->
                    item.url?.let { it1 ->
                        NetworkImage(
                            modifier = Modifier
                                .size(EmojiListDefaults.Icon.Size)
                                .padding(EmojiListDefaults.Icon.ContentPadding)
                                .clickable {
                                    onEmojiSelected.invoke(item)
                                },
                            data = it1,
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            }
        }
    }
}

object EmojiListDefaults {
    object Icon {
        val Size = 48.dp
        val ContentPadding = PaddingValues(4.dp)
    }

    val ContentPadding = PaddingValues(
        horizontal = 8.dp,
        vertical = 0.dp
    )

    object Category {
        val ContentPadding = PaddingValues(vertical = 16.dp, horizontal = 4.dp)
    }
}
