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
package com.twidere.twiderex.viewmodel.compose

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.twidere.services.mastodon.MastodonService
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.extensions.asStateIn
import com.twidere.twiderex.paging.source.MastodonSearchHashtagPagingSource
import com.twidere.twiderex.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class MastodonComposeSearchHashtagViewModel(
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.asStateIn(viewModelScope, null)
    }

    val text = MutableStateFlow("")

    val source = text.debounce(666L).flatMapLatest {
        it.takeIf { it.isNotEmpty() }?.let { str ->
            account.flatMapLatest {
                it?.let { account ->
                    Pager(
                        config = PagingConfig(
                            pageSize = defaultLoadCount,
                            enablePlaceholders = false,
                        )
                    ) {
                        MastodonSearchHashtagPagingSource(
                            str,
                            account.service as MastodonService
                        )
                    }.flow
                } ?: emptyFlow()
            }
        } ?: emptyFlow()
    }.cachedIn(viewModelScope)
}