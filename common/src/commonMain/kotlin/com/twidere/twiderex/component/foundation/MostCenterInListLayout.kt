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

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
// TODO calculate is the most center one
@Composable
fun MostCenterInListLayout(
    modifier: Modifier = Modifier,
    content: @Composable (isMostCenter: Boolean) -> Unit
) {
    // var middleLine = 0.0f
    // val composableScope = rememberCoroutineScope()
    //
    // var isMostCenter = remember {
    //     mutableStateOf(false)
    // }
    // var debounceJob: Job? = null
    Box(
        modifier = modifier
        // modifier = modifier.onGloballyPositioned { coordinates ->
        // if (middleLine == 0.0f) {
        //     var rootCoordinates = coordinates
        //     while (rootCoordinates.parentCoordinates != null) {
        //         rootCoordinates = rootCoordinates.parentCoordinates!!
        //     }
        //     rootCoordinates.boundsInWindow().run {
        //         middleLine = (top + bottom) / 2
        //     }
        // }
        // coordinates.boundsInWindow().run {
        //     VideoPool.setRect(videoKey, this)
        //     if (!isMostCenter && VideoPool.fullInScreen(videoKey, coordinates.size.height)) {
        //         debounceJob?.cancel()
        //         debounceJob = composableScope.launch {
        //             delay(VideoPool.DEBOUNCE_DELAY)
        //             if (VideoPool.isMostCenter(videoKey, middleLine)) {
        //                 isMostCenter = true
        //             }
        //         }
        //     } else if (isMostCenter && !VideoPool.isMostCenter(videoKey, middleLine)) {
        //         isMostCenter = false
        //     }
        // }
        // }
    ) {
        content.invoke(true)
    }
}
