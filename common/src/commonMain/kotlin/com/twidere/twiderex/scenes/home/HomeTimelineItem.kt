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
package com.twidere.twiderex.scenes.home

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.TimelineComponent
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.lazy.LazyListController
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.model.HomeNavigationItem
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.timeline.HomeTimelineViewModel

class HomeTimelineItem : HomeNavigationItem() {

    @Composable
    override fun name(): String = stringResource(com.twidere.twiderex.MR.strings.scene_timeline_title)
    override val route: String
        get() = Root.HomeTimeline

    @Composable
    override fun icon(): Painter = painterResource(res = com.twidere.twiderex.MR.files.ic_home)

    @Composable
    override fun Content() {
        HomeTimelineSceneContent(
            lazyListController = lazyListController
        )
    }

    @Composable
    override fun Fab() {
        HomeTimelineFab()
    }

    override val fabSize: Dp
        get() = HomeTimeLineItemDefaults.FabSize
}

@Composable
fun HomeTimelineScene() {
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_timeline_title))
                    },
                    navigationIcon = {
                        AppBarNavigationButton()
                    }
                )
            },
            floatingActionButton = {
                HomeTimelineFab()
            }
        ) {
            HomeTimelineSceneContent()
        }
    }
}

@Composable
private fun HomeTimelineFab() {
    val navigator = LocalNavigator.current
    FloatingActionButton(
        onClick = {
            navigator.compose(ComposeType.New)
        }
    ) {
        Icon(
            painter = painterResource(res = com.twidere.twiderex.MR.files.ic_feather),
            contentDescription = stringResource(
                res = com.twidere.twiderex.MR.strings.accessibility_scene_home_compose
            )
        )
    }
}

@Composable
fun HomeTimelineSceneContent(
    lazyListController: LazyListController? = null
) {
    val viewModel: HomeTimelineViewModel = getViewModel()
    TimelineComponent(
        viewModel = viewModel,
        lazyListController = lazyListController,
    )
}

private object HomeTimeLineItemDefaults {
    val FabSize = 56.dp
}
