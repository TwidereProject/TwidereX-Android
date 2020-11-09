/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.AmbientInStoryboard
import com.twidere.twiderex.ui.AmbientNavController

@Composable
fun StatusMediaComponent(
    status: UiStatus,
) {
    val inStoryBoard = AmbientInStoryboard.current
    val media = status.media
    val navController = AmbientNavController.current
    val onItemClick = { it: UiMedia ->
        val index = media.indexOf(it)
        if (!inStoryBoard) {
            navController.navigate("media/${status.statusId}?selectedIndex=$index")
        }
    }
    if (media.size == 1) {
        val first = media.first()
        Box(
            modifier = Modifier
                .heightIn(max = 400.dp)
                .aspectRatio(first.width.toFloat() / first.height.toFloat())
                .clip(MaterialTheme.shapes.medium)
        ) {
            StatusMediaPreviewItem(
                media = first,
                onClick = onItemClick,
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(270f / 162f)
                .clip(MaterialTheme.shapes.medium)
        ) {
            if (media.size == 3) {
                Row {
                    media.firstOrNull()?.let {
                        StatusMediaPreviewItem(
                            media = it,
                            modifier = Modifier.weight(1f),
                            onClick = onItemClick,
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        media.drop(1).forEach {
                            StatusMediaPreviewItem(
                                media = it,
                                modifier = Modifier.weight(1f),
                                onClick = onItemClick,
                            )
                        }
                    }
                }
            } else {
                Column {
                    for (i in media.indices.filter { it % 2 == 0 }) {
                        Row(
                            modifier = Modifier.weight(1f),
                        ) {
                            for (y in (i until i + 2)) {
                                media.elementAtOrNull(y)?.let {
                                    StatusMediaPreviewItem(
                                        media = it,
                                        modifier = Modifier.weight(1f),
                                        onClick = onItemClick,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusMediaPreviewItem(
    media: UiMedia,
    modifier: Modifier = Modifier,
    onClick: (UiMedia) -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        media.previewUrl?.let {
            NetworkImage(
                url = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            onClick(media)
                        }
                    ),
            )
        }
    }
}
