/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.component.home

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.compose.navigate
import com.twidere.twiderex.component.TimelineComponent
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.viewmodel.twitter.timeline.HomeTimelineViewModel

class HomeTimelineItem : HomeNavigationItem() {
    override val name: String
        get() = "Home"
    override val icon: VectorAsset
        get() = Icons.Default.Home

    @Composable
    override fun onCompose() {
        val viewModel = viewModel<HomeTimelineViewModel>()
        Scaffold(
            floatingActionButton = {
                val navController = AmbientNavController.current
                FloatingActionButton(
                    onClick = {
                        navController.navigate("compose/New")
                    }
                ) {
                    Icon(asset = Icons.Default.Add)
                }
            }
        ) {
            TimelineComponent(viewModel = viewModel)
        }
    }
}
