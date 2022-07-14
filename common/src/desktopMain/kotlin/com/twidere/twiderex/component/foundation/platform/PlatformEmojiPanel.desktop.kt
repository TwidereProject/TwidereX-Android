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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.model.ui.UiEmoji
import com.twidere.twiderex.model.ui.UiEmojiCategory

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun PlatformEmojiPanel(
    items: List<UiEmojiCategory>,
    showEmoji: Boolean,
    onEmojiSelected: (UiEmoji) -> Unit,
) {
    AnimatedVisibility(
        visible = showEmoji
    ) {
        Box(
            modifier = Modifier
                .height(height = PlatformEmojiPanelDefaults.Height)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            EmojiList(items = items, onEmojiSelected = onEmojiSelected)
        }
    }
}

private object PlatformEmojiPanelDefaults {
    val Height = 200.dp
}
