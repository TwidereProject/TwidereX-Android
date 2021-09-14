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
package com.twidere.twiderex.viewmodel

import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.StatusRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import moe.tlaster.precompose.viewmodel.ViewModel

class StatusViewModel(
    private val statusRepository: StatusRepository,
    private val accountRepository: AccountRepository,
    private val statusKey: MicroBlogKey,
) : ViewModel() {
    private val account by lazy {
        accountRepository.activeAccount
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val status by lazy {
        account.flatMapLatest {
            if (it != null) {
                statusRepository.loadStatus(statusKey = statusKey, accountKey = it.accountKey)
            } else {
                emptyFlow()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val source by lazy {
        account.flatMapLatest {
            if (it != null) {
                statusRepository.conversation(
                    statusKey = statusKey,
                    accountKey = it.accountKey,
                    platformType = it.type,
                    service = it.service
                )
            } else {
                emptyFlow()
            }
        }
    }
}
