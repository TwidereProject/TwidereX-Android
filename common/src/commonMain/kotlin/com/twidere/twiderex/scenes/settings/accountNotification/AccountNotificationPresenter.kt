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
package com.twidere.twiderex.scenes.settings.accountNotification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.findByAccountKeyFlow
import kotlinx.coroutines.flow.Flow

@Composable
fun AccountNotificationPresenter(
  accountKey: MicroBlogKey,
  event: Flow<AccountNotificationEvent>,
  accountRepository: AccountRepository = get(),
): AccountNotificationState {
  val account by accountRepository.findByAccountKeyFlow(accountKey).collectAsState(null)
  val user = remember(account) {
    account?.toUi()
  }
  val preferences = remember { accountRepository.getAccountPreferences(accountKey) }
  val isNotificationEnabled by preferences.isNotificationEnabled.collectAsState(false)

  LaunchedEffect(Unit) {
    event.collect {
      when (it) {
        is AccountNotificationEvent.SetIsNotificationEnabled -> {
          preferences.setIsNotificationEnabled(it.value)
        }
      }
    }
  }

  return AccountNotificationState(
    user = user,
    isNotificationEnabled = isNotificationEnabled
  )
}

data class AccountNotificationState(
  val user: UiUser?,
  val isNotificationEnabled: Boolean,
)

sealed interface AccountNotificationEvent {
  data class SetIsNotificationEnabled(val value: Boolean) : AccountNotificationEvent
}
