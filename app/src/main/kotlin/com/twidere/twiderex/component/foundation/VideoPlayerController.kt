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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ui.PlayerControlView
import com.twidere.twiderex.R

@Composable
fun VideoPlayerController(
    videoControl: PlayerControlView,
    mute: Boolean,
    onMute: (isMute: Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(VideoPlayerControllerDefaults.contentPadding)
    ) {
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { videoControl }
        )
        IconButton(
            onClick = { onMute(!mute) },
        ) {
            Icon(
                painter = painterResource(id = if (mute) R.drawable.ic_volume_mute else R.drawable.ic_volume),
                contentDescription = stringResource(id = R.string.accessibility_common_video_play),
                tint = MaterialTheme.colors.onSurface
            )
        }
    }
}

private object VideoPlayerControllerDefaults {
    val contentPadding = PaddingValues(vertical = 8.dp)
}
