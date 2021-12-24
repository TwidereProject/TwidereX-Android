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

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.model.HomeNavigationItem
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.scenes.dm.DMConversationListSceneContent
import com.twidere.twiderex.scenes.dm.DMConversationListSceneFab

class DMConversationListItem : HomeNavigationItem() {
    @Composable
    override fun name(): String {
        return stringResource(res = com.twidere.twiderex.MR.strings.scene_messages_title)
    }

    override val route: String
        get() = Root.Messages.Home

    @Composable
    override fun icon(): Painter {
        return painterResource(res = com.twidere.twiderex.MR.files.ic_mail)
    }

    @Composable
    override fun Fab() {
        DMConversationListSceneFab()
    }

    @Composable
    override fun Content() {
        DMConversationListSceneContent(
            lazyListController
        )
    }
}
