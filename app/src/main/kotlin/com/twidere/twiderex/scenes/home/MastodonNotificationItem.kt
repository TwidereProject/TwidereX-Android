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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twidere.services.microblog.NotificationService
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.EdgePadding
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.Pager
import com.twidere.twiderex.component.foundation.TextTabsComponent
import com.twidere.twiderex.component.foundation.rememberPagerState
import com.twidere.twiderex.ui.LocalActiveAccount
import kotlinx.coroutines.launch

class MastodonNotificationItem : HomeNavigationItem() {
    @Composable
    override fun name(): String {
        return stringResource(id = R.string.scene_notification_title)
    }

    @Composable
    override fun icon(): Painter {
        return painterResource(id = R.drawable.ic_bell)
    }

    @Composable
    override fun content() {
        val account = LocalActiveAccount.current ?: return
        if (account.service !is NotificationService) {
            return
        }
        val tabs = remember {
            listOf(
                AllNotificationItem(),
                MentionItem(),
            )
        }
        val pagerState = rememberPagerState(maxPage = tabs.lastIndex)
        val scope = rememberCoroutineScope()
        InAppNotificationScaffold(
            topBar = {
                TextTabsComponent(
                    modifier = Modifier.padding(contentPadding),
                    items = tabs.map { it.name() },
                    selectedItem = pagerState.currentPage,
                    onItemSelected = {
                        scope.launch {
                            pagerState.selectPage {
                                pagerState.currentPage = it
                            }
                        }
                    },
                )
            }
        ) {
            Pager(state = pagerState) {
                tabs[page].contentPadding = PaddingValues(0.dp)
                tabs[page].edgePadding = EdgePadding(top = false, bottom = false)
                tabs[page].content()
            }
        }
    }
}
