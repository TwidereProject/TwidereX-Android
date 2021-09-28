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

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.component.TimelineComponent
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.lazy.LazyListController
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.model.HomeNavigationItem
import com.twidere.twiderex.navigation.RootRoute
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.timeline.MentionsTimelineViewModel

class MentionItem : HomeNavigationItem() {
    @Composable
    override fun name(): String = stringResource(com.twidere.common.R.string.scene_mentions_title)
    override val route: String
        get() = RootRoute.Mentions

    @Composable
    override fun icon(): Painter = painterResource(id = R.drawable.ic_message_circle)

    @Composable
    override fun Content() {
        val viewModel: MentionsTimelineViewModel = getViewModel()
        TimelineComponent(
            viewModel = viewModel,
            lazyListController = lazyListController,
        )
    }
}

@Composable
fun MentionScene() {
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    title = {
                        Text(text = stringResource(id = com.twidere.common.R.string.scene_mentions_title))
                    },
                    navigationIcon = {
                        AppBarNavigationButton()
                    }
                )
            }
        ) {
            MentionSceneContent()
        }
    }
}

@Composable
fun MentionSceneContent(
    lazyListController: LazyListController? = null
) {
    val viewModel: MentionsTimelineViewModel = getViewModel()
    TimelineComponent(
        viewModel = viewModel,
        lazyListController = lazyListController,
    )
}
