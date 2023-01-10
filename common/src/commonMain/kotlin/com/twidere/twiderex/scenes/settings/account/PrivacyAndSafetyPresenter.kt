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
package com.twidere.twiderex.scenes.settings.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.preferences.model.AccountPreferences
import kotlinx.coroutines.flow.Flow

@Composable
fun PrivacyAndSafetyPresenter(
  event: Flow<PrivacyAndSafetyEvent>,
  preferencesHolder: PreferencesHolder = get(),
): PrivacyAndSafetyState {
  val accountPreferences by remember {
    preferencesHolder.accountPreferences.data
  }.collectAsState(AccountPreferences())
  LaunchedEffect(Unit) {
    event.collect { event ->
      when (event) {
        is PrivacyAndSafetyEvent.SetIsAlwaysShowSensitiveMedia -> {
          preferencesHolder.accountPreferences.updateData {
            it.copy(isAlwaysShowSensitiveMedia = event.bool)
          }
        }
      }
    }
  }
  return PrivacyAndSafetyState(
    account = accountPreferences,
  )
}

sealed interface PrivacyAndSafetyEvent {
  data class SetIsAlwaysShowSensitiveMedia(val bool: Boolean) : PrivacyAndSafetyEvent
}

@Immutable
data class PrivacyAndSafetyState(
  val account: AccountPreferences,
)
