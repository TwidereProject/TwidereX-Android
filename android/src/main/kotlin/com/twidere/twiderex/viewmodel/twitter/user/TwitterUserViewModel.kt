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
package com.twidere.twiderex.viewmodel.twitter.user

import com.twidere.services.microblog.LookupService
import com.twidere.twiderex.ext.asStateIn
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.UserRepository
import com.twidere.twiderex.utils.notify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class TwitterUserViewModel(
    private val repository: UserRepository,
    private val inAppNotification: InAppNotification,
    private val accountRepository: AccountRepository,
    private val screenName: String,
) : ViewModel() {

    private val account by lazy {
        accountRepository.activeAccount.asStateIn(viewModelScope, null)
    }

    val error = MutableStateFlow<Throwable?>(null)

    val user by lazy {
        account.map {
            if (it != null) {
                try {
                    repository.lookupUserByName(
                        screenName,
                        accountKey = it.accountKey,
                        lookupService = it.service as LookupService,
                    )
                } catch (e: Throwable) {
                    e.notify(inAppNotification)
                    error.value = e
                    null
                }
            } else {
                null
            }
        }
    }
}
