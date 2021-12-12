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
package com.twidere.twiderex.kmp

import androidx.compose.ui.input.key.KeyEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GlobalKeyEventHolder {

    private val _keyEventFlow = MutableSharedFlow<KeyEvent?>(1)

    val keyEventFlow = _keyEventFlow.asSharedFlow()

    fun onKeyEvent(event: KeyEvent) {
        _keyEventFlow.tryEmit(event)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun clearBefore() {
        _keyEventFlow.resetReplayCache()
    }
}
