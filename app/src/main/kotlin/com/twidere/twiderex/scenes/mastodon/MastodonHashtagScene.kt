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
package com.twidere.twiderex.scenes.mastodon

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.SwipeToRefreshLayout
import com.twidere.twiderex.component.lazy.collectAsLazyPagingItems
import com.twidere.twiderex.component.lazy.ui.LazyUiStatusList
import com.twidere.twiderex.di.assisted.assistedViewModel
import com.twidere.twiderex.extensions.refreshOrRetry
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.viewmodel.mastodon.MastodonHashtagViewModel

@Composable
fun MastodonHashtagScene(keyword: String) {
    val account = LocalActiveAccount.current ?: return
    val viewModel =
        assistedViewModel<MastodonHashtagViewModel.AssistedFactory, MastodonHashtagViewModel>(
            keyword,
            account,
        ) {
            it.create(keyword = keyword, account = account)
        }
    val source = viewModel.source.collectAsLazyPagingItems()
    TwidereXTheme {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = keyword)
                    }
                )
            }
        ) {
            SwipeToRefreshLayout(
                refreshingState = source.loadState.refresh is LoadState.Loading,
                onRefresh = {
                    source.refreshOrRetry()
                },
            ) {
                LazyUiStatusList(items = source)
            }
        }
    }
}
