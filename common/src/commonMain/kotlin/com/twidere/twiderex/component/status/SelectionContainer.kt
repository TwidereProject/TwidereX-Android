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
package com.twidere.twiderex.component.status

import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import com.twidere.twiderex.kmp.Platform
import com.twidere.twiderex.kmp.currentPlatform
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PositionWrapper {
    var action: ((Offset) -> Unit)? = null
    fun onchange(offset: Offset) {
        action?.invoke(offset)
    }
}

// FIXME: 2021/11/26  workaround for https://github.com/JetBrains/compose-jb/issues/1450
@Composable
fun SelectionContainer(
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    content: @Composable (PositionWrapper?) -> Unit,
) {
    if (!enable) {
        content.invoke(null)
        return
    }
    val positionWrapper = remember {
        if (currentPlatform != Platform.Android) PositionWrapper() else null
    }
    androidx.compose.foundation.text.selection.SelectionContainer(
        modifier = if (currentPlatform != Platform.Android) {
            modifier.pointerInput(Unit) {
                forEachGesture {
                    coroutineScope {
                        awaitPointerEventScope {
                            awaitPointerEvent().apply {
                                this.takeIf {
                                    it.type == PointerEventType.Press
                                }?.let {
                                    launch {
                                        for (i in 0 until 15) {
                                            delay(10)
                                            if (
                                                currentEvent.type == PointerEventType.Release
                                            ) {
                                                if (
                                                    currentEvent.changes[0].position.minus(it.changes[0].position).getDistance() < viewConfiguration.touchSlop
                                                ) {
                                                    positionWrapper?.onchange(it.changes[0].position)
                                                }
                                                cancel()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            modifier
        }
    ) {
        content.invoke(positionWrapper)
    }
}
