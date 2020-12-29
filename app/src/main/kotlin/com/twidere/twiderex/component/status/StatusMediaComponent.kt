/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.component.status

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.NetworkImage
import com.twidere.twiderex.component.foundation.VideoPlayer
import com.twidere.twiderex.component.navigation.AmbientNavigator
import com.twidere.twiderex.model.MediaType
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiStatus

@Composable
fun StatusMediaComponent(
    status: UiStatus,
) {
    val navigator = AmbientNavigator.current
    val media = status.media
    val onItemClick = { it: UiMedia ->
        val index = media.indexOf(it)
        navigator.media(statusKey = status.statusKey, selectedIndex = index)
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
        when (media.type) {
            MediaType.photo ->
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
            MediaType.video, MediaType.animated_gif -> media.mediaUrl?.let {
                VideoPlayer(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            onClick = {
                                onClick(media)
                            }
                        ),
                    url = it,
                    showControls = false,
                    volume = 0F,
                )
            }
        }
    }
}
