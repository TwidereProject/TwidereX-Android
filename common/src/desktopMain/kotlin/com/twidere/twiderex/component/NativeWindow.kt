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
package com.twidere.twiderex.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.twidere.twiderex.ui.TwidereTheme
import com.twidere.twiderex.ui.isDarkTheme
import com.twidere.twiderex.utils.OperatingSystem
import com.twidere.twiderex.utils.currentOperatingSystem
import moe.tlaster.precompose.PreComposeWindow

@Composable
fun NativeWindow(
    onCloseRequest: () -> Unit,
    state: WindowState = rememberWindowState(),
    visible: Boolean = true,
    title: String = "Untitled",
    icon: Painter? = null,
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    alwaysOnTop: Boolean = false,
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    content: @Composable FrameWindowScope.() -> Unit,
) {
    PreComposeWindow(
        state = state,
        visible = visible,
        title = title,
        icon = icon,
        undecorated = true,
        transparent = true,
        resizable = resizable,
        enabled = enabled,
        focusable = focusable,
        alwaysOnTop = alwaysOnTop,
        onPreviewKeyEvent = onPreviewKeyEvent,
        onKeyEvent = onKeyEvent,
        onCloseRequest = onCloseRequest,
        content = {
            Column(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)),
            ) {
                PlatformTitleBar(
                    title = title,
                    icon = icon,
                    operatingSystem = currentOperatingSystem,
                    onCloseRequest = onCloseRequest,
                    onMinimizeRequest = { state.isMinimized = true },
                    onMaximizeRequest = { state.placement = WindowPlacement.Maximized },
                    onUndoMaximizeRequest = { state.placement = WindowPlacement.Floating },
                    isMaximized = state.placement == WindowPlacement.Maximized,
                )
                content.invoke(this@PreComposeWindow)
            }
        },
    )
}

@Composable
private fun WindowScope.PlatformTitleBar(
    title: String,
    icon: Painter?,
    operatingSystem: OperatingSystem,
    onCloseRequest: () -> Unit,
    onMinimizeRequest: () -> Unit,
    onMaximizeRequest: () -> Unit,
    onUndoMaximizeRequest: () -> Unit,
    isMaximized: Boolean = false,
) {
    WindowDraggableArea {
        TwidereTheme(isDarkTheme()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                when (operatingSystem) {
                    OperatingSystem.MacOS -> OSXTitleBar(
                        title = title,
                        icon = icon,
                        onCloseRequest = onCloseRequest,
                        onMinimizeRequest = onMinimizeRequest,
                        onMaximizeRequest = onMaximizeRequest,
                        onUndoMaximizeRequest = onUndoMaximizeRequest,
                        isMaximized = isMaximized
                    )
                    else -> WindowsTitleBar(
                        title = title,
                        icon = icon,
                        onCloseRequest = onCloseRequest,
                        onMinimizeRequest = onMinimizeRequest,
                        onMaximizeRequest = onMaximizeRequest,
                        onUndoMaximizeRequest = onUndoMaximizeRequest,
                        isMaximized = isMaximized
                    )
                }
            }
        }
    }
}

@Composable
private fun WindowsTitleBar(
    title: String,
    icon: Painter?,
    onCloseRequest: () -> Unit,
    onMinimizeRequest: () -> Unit,
    onMaximizeRequest: () -> Unit,
    onUndoMaximizeRequest: () -> Unit,
    isMaximized: Boolean = false,
) {
    Row(
        modifier = Modifier
            .padding(
                start = 16.dp,
                top = 8.dp,
                end = 8.dp,
                bottom = 8.dp
            )
    ) {
        WindowTitle(
            title = title,
            icon = icon,
        )
        Spacer(modifier = Modifier.weight(1f))
        WindowsWindowButtons(
            onCloseRequest = onCloseRequest,
            onMinimizeRequest = onMinimizeRequest,
            onMaximizeRequest = onMaximizeRequest,
            onUndoMaximizeRequest = onUndoMaximizeRequest,
            isMaximized = isMaximized
        )
    }
}

@Composable
private fun WindowsWindowButtons(
    onCloseRequest: () -> Unit,
    onMinimizeRequest: () -> Unit,
    onMaximizeRequest: () -> Unit,
    onUndoMaximizeRequest: () -> Unit,
    isMaximized: Boolean = false,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        WindowButton(
            icon = Icons.Filled.Minimize,
            onClick = onMinimizeRequest
        )
        if (isMaximized) {
            WindowButton(
                icon = Icons.Filled.FullscreenExit,
                onClick = onUndoMaximizeRequest
            )
        } else {
            WindowButton(
                icon = Icons.Filled.Fullscreen,
                onClick = onMaximizeRequest
            )
        }
        WindowButton(
            icon = Icons.Filled.Close,
            onClick = onCloseRequest
        )
    }
}

@Composable
private fun WindowButton(icon: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(24.dp)
    ) {
        Icon(icon, contentDescription = null)
    }
}

@Composable
private fun WindowTitle(
    modifier: Modifier = Modifier,
    title: String,
    icon: Painter?,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Image(painter = icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = title)
    }
}

@Composable
private fun OSXTitleBar(
    title: String,
    icon: Painter?,
    onCloseRequest: () -> Unit,
    onMinimizeRequest: () -> Unit,
    onMaximizeRequest: () -> Unit,
    onUndoMaximizeRequest: () -> Unit,
    isMaximized: Boolean = false,
) {
    Box(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        OSXWindowButtons(
            onCloseRequest = onCloseRequest,
            onMinimizeRequest = onMinimizeRequest,
            onMaximizeRequest = onMaximizeRequest,
            onUndoMaximizeRequest = onUndoMaximizeRequest,
            isMaximized = isMaximized
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            WindowTitle(
                title = title,
                icon = icon,
            )
        }
    }
}

@Composable
private fun OSXWindowButtons(
    onCloseRequest: () -> Unit,
    onMinimizeRequest: () -> Unit,
    onMaximizeRequest: () -> Unit,
    onUndoMaximizeRequest: () -> Unit,
    isMaximized: Boolean = false,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(Color(0xFFff5150), shape = CircleShape)
                .clickable(onClick = onCloseRequest)
                .clip(CircleShape)
        )
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(Color(0xFFffbc00), shape = CircleShape)
                .clickable(onClick = onMinimizeRequest)
                .clip(CircleShape)
        )
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(Color(0xFF00cd1d), shape = CircleShape)
                .clickable(onClick = {
                    if (isMaximized) {
                        onUndoMaximizeRequest()
                    } else {
                        onMaximizeRequest()
                    }
                })
                .clip(CircleShape)
        )
    }
}
