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
package com.twidere.twiderex.viewmodel.dm

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.twidere.services.microblog.SearchService
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.ext.asStateIn
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.paging.source.SearchUserPagingSource
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.DirectMessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class DMNewConversationViewModel(
    private val dmRepository: DirectMessageRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.asStateIn(viewModelScope, null)
    }

    val input = MutableStateFlow("")

    val sourceFlow = input.debounce(666L).flatMapLatest {
        it.takeIf { it.isNotEmpty() }?.let { str ->
            account.flatMapLatest {
                it?.let { account ->
                    Pager(
                        config = PagingConfig(
                            pageSize = defaultLoadCount,
                            enablePlaceholders = false,
                        )
                    ) {
                        SearchUserPagingSource(
                            accountKey = account.accountKey,
                            str,
                            account.service as SearchService,
                        )
                    }.flow
                } ?: emptyFlow()
            }
        } ?: emptyFlow()
    }.cachedIn(viewModelScope)

    fun createNewConversation(receiver: UiUser, onResult: (key: MicroBlogKey?) -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching {
                account.lastOrNull()?.let { account ->
                    dmRepository.createNewConversation(
                        receiver = receiver,
                        accountKey = account.accountKey,
                        platformType = account.type
                    )
                }
            }.onSuccess {
                onResult(it)
            }.onFailure {
                onResult(null)
            }
        }
    }
}