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
package com.twidere.twiderex.component.foundation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.twidere.twiderex.utils.video.VideoPool
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MostCenterInListLayout(
    videoKey: String,
    modifier: Modifier = Modifier,
    content: @Composable (isMostCenter: Boolean) -> Unit
) {
    var middleLine = 0.0f
    val composableScope = rememberCoroutineScope()

    var isMostCenter by remember {
        mutableStateOf(false)
    }
    var debounceJob: Job? = null
    DisposableEffect(Unit) {
        onDispose {
            VideoPool.removeRect(videoKey)
        }
    }
    Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            if (middleLine == 0.0f) {
                var rootCoordinates = coordinates
                while (rootCoordinates.parentCoordinates != null) {
                    rootCoordinates = rootCoordinates.parentCoordinates!!
                }
                rootCoordinates.boundsInWindow().run {
                    middleLine = (top + bottom) / 2
                }
            }
            coordinates.boundsInWindow().run {
                VideoPool.setRect(videoKey, this)
                if (!isMostCenter) {
                    debounceJob?.cancel()
                    debounceJob = composableScope.launch {
                        delay(VideoPool.DEBOUNCE_DELAY)
                        if (VideoPool.isMostCenter(videoKey, middleLine)) {
                            isMostCenter = true
                        }
                    }
                } else if (isMostCenter && !VideoPool.isMostCenter(videoKey, middleLine)) {
                    isMostCenter = false
                }
            }
        }
    ) {
        content.invoke(isMostCenter)
    }
}
