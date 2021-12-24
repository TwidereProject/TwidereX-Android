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
package com.twidere.twiderex.viewmodel.user

import androidx.paging.cachedIn
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.extensions.asStateIn
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.TimelineRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.mapNotNull
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class UserTimelineViewModel(
    private val repository: TimelineRepository,
    private val accountRepository: AccountRepository,
    userKey: MicroBlogKey,
) : ViewModel() {
    private val _excludeReplies = MutableStateFlow(false)
    val excludeReplies = _excludeReplies.asStateIn(viewModelScope, false)
    private val account by lazy {
        accountRepository.activeAccount.mapNotNull { it }
    }

    fun setExcludeReplies(value: Boolean) {
        _excludeReplies.value = value
    }

    @OptIn(FlowPreview::class)
    val source by lazy {
        combine(account, _excludeReplies) { account, excludeReplies ->
            repository.userTimeline(
                userKey = userKey,
                accountKey = account.accountKey,
                service = account.service as TimelineService,
                exclude_replies = excludeReplies,
            )
        }.flattenMerge().cachedIn(viewModelScope)
    }
}
