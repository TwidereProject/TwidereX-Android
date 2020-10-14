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
package com.twidere.twiderex.fragment

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.fragment.navArgs
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.ExpandedStatusComponent
import com.twidere.twiderex.component.StatusDivider
import com.twidere.twiderex.component.TimelineStatusComponent
import com.twidere.twiderex.component.loading
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.twitter.TwitterStatusViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.max

@AndroidEntryPoint
class StatusFragment : JetFragment() {

    private val args by navArgs<StatusFragmentArgs>()

    @OptIn(ExperimentalLazyDsl::class)
    @Composable
    override fun onCompose() {
        val viewModel = viewModel<TwitterStatusViewModel>()
        val loadingPrevious by viewModel.loadingPrevious.observeAsState(initial = false)
        val loadingMore by viewModel.loadingMore.observeAsState(initial = false)
        val status by viewModel.status.observeAsState(initial = args.status)
        val items by viewModel.items.observeAsState(initial = emptyList())
        val index by viewModel.currentStatusIndex.observeAsState(initial = 0)
        launchInComposition {
            viewModel.init(args.status)
        }
        Scaffold(
            topBar = {
                AppBar(
                    title = {
                        Text(text = "Tweet")
                    },
                    navigationIcon = {
                        AppBarNavigationButton()
                    }
                )
            }
        ) {
            if (loadingPrevious) {
                LazyColumn {
                    loading()
                    item {
                        ExpandedStatusComponent(
                            status = status,
                        )
                    }
                    loading()
                }
            } else {
                if (!items.any()) {
                    ExpandedStatusComponent(
                        status = status,
                    )
                } else {
                    LazyColumnForIndexed(
                        items = items,
                        state = rememberLazyListState(initialFirstVisibleItemIndex = max(index, 0))
                    ) { index, item ->
                        if (item.statusId == status.statusId) {
                            ExpandedStatusComponent(
                                status = item,
                            )
                            Divider(
                                modifier = Modifier.padding(horizontal = standardPadding * 2)
                            )
                        } else {
                            TimelineStatusComponent(data = item)
                            if (index != items.size - 1) {
                                StatusDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}
