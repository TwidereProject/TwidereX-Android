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
package com.twidere.twiderex.model

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.FabPosition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.lazy.LazyListController

abstract class HomeNavigationItem {

    @Composable
    abstract fun name(): String

    abstract val route: String

    @Composable
    abstract fun icon(): Painter
    open val withAppBar = true
    open val lazyListController = LazyListController()

    @Composable
    abstract fun Content()

    @Composable
    open fun Fab() {
        // implement this method to apply FloatingActionButton
        // FIXME: 2021/6/17 Workaround for Scaffold#256 which will filter out fab when size == 0
        Spacer(modifier = Modifier.sizeIn(minWidth = 1.dp, minHeight = 1.dp))
    }

    open val floatingActionButtonPosition = FabPosition.End

    // offset to hide fab when scroll timeline
    open val fabSize = 0.dp
}
