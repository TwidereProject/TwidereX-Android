/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.twidere.twiderex.component.lazy.LazyColumn2
import com.twidere.twiderex.component.lazy.itemsPagingGridIndexed
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.status.StatusMediaPreviewItem
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.ui.LocalVideoPlayback

@Composable
fun LazyUiStatusImageList(
    items: LazyPagingItems<Pair<UiMedia, UiStatus>>,
) {
    LazyUiList(items = items) {
        LazyColumn2 {
            item {
                Box(modifier = Modifier.height(LazyUiStatusImageListDefaults.Spacing))
            }
            itemsPagingGridIndexed(
                items,
                rowSize = 2,
                spacing = LazyUiStatusImageListDefaults.Spacing,
                padding = LazyUiStatusImageListDefaults.Spacing
            ) { index, pair ->
                pair?.let { item ->
                    val navigator = LocalNavigator.current
                    CompositionLocalProvider(
                        LocalVideoPlayback provides DisplayPreferences.AutoPlayback.Off,
                    ) {
                        StatusMediaPreviewItem(
                            item.first,
                            modifier = Modifier
                                .aspectRatio(1F)
                                .clip(
                                    MaterialTheme.shapes.medium
                                ),
                            onClick = {
                                navigator.media(item.second.statusKey, index)
                            }
                        )
                    }
                }
            }
            item {
                Box(modifier = Modifier.height(LazyUiStatusImageListDefaults.Spacing))
            }
        }
    }
}

object LazyUiStatusImageListDefaults {
    val Spacing = 8.dp
}
