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
package com.twidere.twiderex.scenes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.ui.LocalNavController
import moe.tlaster.precompose.PreComposeWindow

@Composable
actual fun PlatformStatusMediaScene(statusKey: MicroBlogKey, selectedIndex: Int) {
    DialogScene {
        StatusMediaScene(statusKey = statusKey, selectedIndex = selectedIndex)
    }
}

@Composable
actual fun PlatformRawMediaScene(url: String, type: MediaType) {
    DialogScene {
        RawMediaScene(url = url, type = type)
    }
}

@Composable
actual fun PlatformPureMediaScene(belongToKey: MicroBlogKey, selectedIndex: Int) {
    DialogScene {
        PureMediaScene(belongToKey = belongToKey, selectedIndex = selectedIndex)
    }
}

@Composable
actual fun StatusMediaSceneLayout(
    backgroundColor: Color,
    contentColor: Color,
    closeButton: @Composable () -> Unit,
    bottomView: @Composable () -> Unit,
    mediaView: @Composable () -> Unit,
    onClick: () -> Unit
) {
    InAppNotificationScaffold(
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        bottomBar = {
        }
    ) {
        val context = currentCompositionLocalContext
        SwingPanel(
            modifier = Modifier.fillMaxSize()
                .clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            factory = {
                ComposePanel().apply {
                    setContent {
                        CompositionLocalProvider(
                            context = context
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    mediaView.invoke()
                                }
                                // TODO  FIXEME: place SwingPanel to bottom
                                SwingPanel(
                                    modifier = Modifier.align(Alignment.BottomCenter),
                                    factory = {
                                        ComposePanel().apply {
                                            setContent {
                                                CompositionLocalProvider(
                                                    context = context
                                                ) {
                                                    bottomView.invoke()
                                                }
                                            }
                                        }
                                    }
                                )
                                SwingPanel(
                                    factory = {
                                        ComposePanel().apply {
                                            setContent {
                                                CompositionLocalProvider(
                                                    context = context
                                                ) {
                                                    closeButton.invoke()
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun DialogScene(content: @Composable () -> Unit) {
    val navController = LocalNavController.current
    PreComposeWindow(
        onCloseRequest = {
            navController.goBack()
        },
        title = "",
        content = {
            content.invoke()
        },
    )
}
