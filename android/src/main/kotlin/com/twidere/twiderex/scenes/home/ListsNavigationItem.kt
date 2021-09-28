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
package com.twidere.twiderex.scenes.home

import androidx.compose.material.FabPosition
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.model.HomeNavigationItem
import com.twidere.twiderex.navigation.RootRoute
import com.twidere.twiderex.scenes.lists.ListsSceneContent
import com.twidere.twiderex.scenes.lists.ListsSceneFab

class ListsNavigationItem : HomeNavigationItem() {
    @Composable
    override fun name(): String {
        return stringResource(id = com.twidere.common.R.string.scene_lists_title)
    }

    override val route: String
        get() = RootRoute.Lists.Home

    @Composable
    override fun icon(): Painter {
        return painterResource(id = R.drawable.ic_lists)
    }

    @Composable
    override fun Fab() {
        ListsSceneFab()
    }

    override val floatingActionButtonPosition: FabPosition
        get() = FabPosition.Center

    @Composable
    override fun Content() {
        ListsSceneContent()
    }
}
