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
package com.twidere.twiderex.viewmodel.lists

import androidx.paging.cachedIn
import com.twidere.twiderex.ext.asStateIn
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.TimelineRepository
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class ListsTimelineViewModel(
    repository: TimelineRepository,
    private val accountRepository: AccountRepository,
    listKey: MicroBlogKey,
) : ViewModel() {
    private val account by lazy {
        accountRepository.activeAccount.asStateIn(viewModelScope, null)
    }

    val source by lazy {
        account.flatMapLatest {
            it?.let {
                repository.listTimeline(
                    listKey = listKey,
                    accountKey = it.accountKey,
                    service = it.service as TimelineService
                )
            } ?: emptyFlow()
        }.cachedIn(viewModelScope)
    }
}
