/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.vectorResource
import com.twidere.twiderex.R
import com.twidere.twiderex.component.TimelineComponent
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.viewmodel.twitter.timeline.MentionsTimelineViewModel

class MentionItem : HomeNavigationItem() {
    override val name: String
        get() = "Mentions"
    override val route: String
        get() = "mentions"

    @Composable
    override val icon: VectorAsset
        get() = vectorResource(id = R.drawable.ic_message_circle)

    @Composable
    override fun onCompose() {
        val account = AmbientActiveAccount.current ?: return
        val viewModel =
            assistedViewModel<MentionsTimelineViewModel.AssistedFactory, MentionsTimelineViewModel>(
                account
            ) {
                it.create(account)
            }
        TimelineComponent(viewModel = viewModel)
    }
}
