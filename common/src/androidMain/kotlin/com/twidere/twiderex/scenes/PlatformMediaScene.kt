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
package com.twidere.twiderex.scenes

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MediaType

@Composable
actual fun PlatformStatusMediaScene(statusKey: MicroBlogKey, selectedIndex: Int) {
    StatusMediaScene(statusKey = statusKey, selectedIndex = selectedIndex)
}

@Composable
actual fun PlatformRawMediaScene(url: String, type: MediaType) {
    RawMediaScene(url = url, type = type)
}

@Composable
actual fun PlatformPureMediaScene(belongToKey: MicroBlogKey, selectedIndex: Int) {
    PureMediaScene(belongToKey = belongToKey, selectedIndex = selectedIndex)
}

@Composable
actual fun StatusMediaSceneLayout(
    backgroundColor: Color,
    contentColor: Color,
    closeButton: @Composable () -> Unit,
    bottomView: @Composable () -> Unit,
    mediaView: @Composable () -> Unit,
    backgroundView: @Composable () -> Unit,
) {
    InAppNotificationScaffold(
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        bottomBar = {
            bottomView.invoke()
        }
    ) {
        Box {
            backgroundView.invoke()
            mediaView.invoke()
            closeButton.invoke()
        }
    }
}
