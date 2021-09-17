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
package com.twidere.twiderex.component.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

expect fun VideoPlayerImpl(
    modifier: Modifier = Modifier,
    url: String,
    volume: Float = 1f,
    customControl: Any? = null,
    showControls: Boolean = customControl == null,
    zOrderMediaOverlay: Boolean = false,
    keepScreenOn: Boolean = false,
    isListItem: Boolean = true,
    thumb: @Composable (() -> Unit)? = null,
)

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    url: String,
    volume: Float = 1f,
    customControl: Any? = null,
    showControls: Boolean = customControl == null,
    zOrderMediaOverlay: Boolean = false,
    keepScreenOn: Boolean = false,
    isListItem: Boolean = true,
    thumb: @Composable (() -> Unit)? = null,
) {
    VideoPlayerImpl(
        modifier = modifier,
        url = url,
        volume = volume,
        customControl = customControl,
        showControls = showControls,
        zOrderMediaOverlay = zOrderMediaOverlay,
        keepScreenOn = keepScreenOn,
        isListItem = isListItem,
        thumb = thumb
    )
}

@Composable
fun VideoPreview() {
    LazyColumn {
        for (i in 0..5) {
            item {
                Box(
                    modifier = Modifier
                        .background(Color.DarkGray)
                        .padding(100.dp)
                ) {
                    VideoPlayer(
                        url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        modifier = Modifier.width(500.dp).height(500.dp)
                    )
                }
            }
        }
    }
}
