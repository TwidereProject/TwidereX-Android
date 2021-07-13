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

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twidere.services.microblog.NotificationService
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.Pager
import com.twidere.twiderex.component.foundation.TextTabsComponent
import com.twidere.twiderex.component.foundation.rememberPagerState
import com.twidere.twiderex.component.lazy.LazyListController
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereScene
import kotlinx.coroutines.launch

class MastodonNotificationItem : HomeNavigationItem() {
    @Composable
    override fun name(): String {
        return stringResource(id = R.string.scene_notification_title)
    }

    override val route: String
        get() = Route.Mastodon.Notification

    @Composable
    override fun icon(): Painter {
        return painterResource(id = R.drawable.ic_bell)
    }

    override var lazyListController: LazyListController = LazyListController()

    @Composable
    override fun Content() {
        MastodonNotificationSceneContent(
            setLazyListController = {
                lazyListController = it
            }
        )
    }
}

@Composable
fun MastodonNotificationScene() {
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    title = {
                        Text(text = stringResource(id = R.string.scene_notification_title))
                    },
                    navigationIcon = {
                        AppBarNavigationButton()
                    }
                )
            }
        ) {
            MastodonNotificationSceneContent()
        }
    }
}

@Composable
fun MastodonNotificationSceneContent(
    setLazyListController: ((lazyListController: LazyListController) -> Unit)? = null,
) {
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
    val pagerState = rememberPagerState(pageCount = tabs.size)
    LaunchedEffect(pagerState.currentPage) {
        // FIXME: 2021/5/17 A little bit dirty
        setLazyListController?.invoke(tabs[pagerState.currentPage].lazyListController)
    }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TextTabsComponent(
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
            tabs[page].Content()
        }
    }
}
