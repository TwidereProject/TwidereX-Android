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
package com.twidere.twiderex.scenes.settings.misc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.preferences.model.MiscPreferences
import com.twidere.twiderex.repository.NitterRepository
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow

@Composable
fun MiscPresenter(
  event: Flow<MiscEvent>,
): MiscState {
  val account = CurrentAccountPresenter()
  if (account !is CurrentAccountState.Account) {
    return MiscState.NoAccount
  }
  val user = remember(account.account) {
    account.account.toUi()
  }

  val nitterChannel = remember { Channel<NitterEvent>() }
  val nitterFlow = remember { nitterChannel.consumeAsFlow() }
  val nitterState = NitterPresenter(nitterFlow, account.account)

  val proxyChannel = remember { Channel<ProxyEvent>() }
  val proxyFlow = remember { proxyChannel.consumeAsFlow() }
  val proxyState = ProxyPresenter(proxyFlow)

  LaunchedEffect(Unit) {
    event.collect { event ->
      when (event) {
        is MiscEvent.Nitter -> nitterChannel.trySend(event.event)
        is MiscEvent.Proxy -> proxyChannel.trySend(event.event)
      }
    }
  }

  return MiscState.State(
    user = user,
    nitterState = nitterState,
    proxyState = proxyState,
  )
}

sealed interface MiscState {
  object NoAccount : MiscState
  data class State(
    val user: UiUser,
    val nitterState: NitterState,
    val proxyState: ProxyState,
  ) : MiscState
}

sealed interface MiscEvent {
  data class Proxy(val event: ProxyEvent) : MiscEvent
  data class Nitter(val event: NitterEvent) : MiscEvent
}

@Composable
fun ProxyPresenter(
  flow: Flow<ProxyEvent>,
  preferencesHolder: PreferencesHolder = get(),
): ProxyState {
  val preferences by preferencesHolder.miscPreferences.data.collectAsState(MiscPreferences())

  var showProxyTypeDialog by remember { mutableStateOf(false) }
  var showProxyHostDialog by remember { mutableStateOf(false) }
  var showProxyPortDialog by remember { mutableStateOf(false) }
  var showProxyUserNameDialog by remember { mutableStateOf(false) }
  var showProxyPasswordDialog by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    flow.collect { event ->
      when (event) {
        is ProxyEvent.ShowProxyTypeDialog -> showProxyTypeDialog = true
        is ProxyEvent.ShowProxyHostDialog -> showProxyHostDialog = true
        is ProxyEvent.ShowProxyPortDialog -> showProxyPortDialog = true
        is ProxyEvent.ShowProxyUserNameDialog -> showProxyUserNameDialog = true
        is ProxyEvent.ShowProxyPasswordDialog -> showProxyPasswordDialog = true
        is ProxyEvent.ProxyEnabledChanged -> preferencesHolder.miscPreferences.updateData {
          it.copy(useProxy = event.enabled)
        }
      }
    }
  }

  return ProxyState(
    proxyHost = preferences.proxyServer,
    proxyPort = preferences.proxyPort,
    proxyType = preferences.proxyType,
    proxyEnabled = preferences.useProxy,
    proxyUserName = preferences.proxyUserName,
    proxyPassword = preferences.proxyPassword,

    showProxyHostDialog = showProxyHostDialog,
    showProxyPortDialog = showProxyPortDialog,
    showProxyTypeDialog = showProxyTypeDialog,
    showProxyUserNameDialog = showProxyUserNameDialog,
    showProxyPasswordDialog = showProxyPasswordDialog,
  )
}

data class ProxyState(
  val proxyEnabled: Boolean,
  val proxyType: MiscPreferences.ProxyType,
  val proxyHost: String,
  val proxyPort: Int,
  val proxyUserName: String,
  val proxyPassword: String,

  val showProxyTypeDialog: Boolean,
  val showProxyHostDialog: Boolean,
  val showProxyPortDialog: Boolean,
  val showProxyUserNameDialog: Boolean,
  val showProxyPasswordDialog: Boolean,
)

sealed interface ProxyEvent {
  data class ProxyEnabledChanged(val enabled: Boolean) : ProxyEvent
  // data class ProxyTypeChanged(val type: MiscPreferences.ProxyType) : ProxyEvent
  // data class ProxyHostChanged(val host: String) : ProxyEvent
  // data class ProxyPortChanged(val port: Int) : ProxyEvent
  // data class ProxyUserNameChanged(val userName: String) : ProxyEvent
  // data class ProxyPasswordChanged(val password: String) : ProxyEvent

  data class ShowProxyTypeDialog(val show: Boolean) : ProxyEvent
  data class ShowProxyHostDialog(val show: Boolean) : ProxyEvent
  data class ShowProxyPortDialog(val show: Boolean) : ProxyEvent
  data class ShowProxyUserNameDialog(val show: Boolean) : ProxyEvent
  data class ShowProxyPasswordDialog(val show: Boolean) : ProxyEvent
}

@Composable
fun NitterPresenter(
  event: Flow<NitterEvent>,
  account: AccountDetails,
  nitterRepository: NitterRepository = get(),
  preferencesHolder: PreferencesHolder = get(),
): NitterState {
  val preferences by preferencesHolder.miscPreferences.data.collectAsState(MiscPreferences())
  var nitter by remember { mutableStateOf(TextFieldValue(preferences.nitterInstance)) }

  val isNitterInputValid =
    (nitter.text.startsWith("https://") || nitter.text.startsWith("http://")) && !nitter.text.endsWith(
      "/"
    )
  var nitterVerifyLoading by remember { mutableStateOf(false) }
  var nitterVerify by remember { mutableStateOf(false) }

  var showInformationDialog by remember { mutableStateOf(false) }
  var showUsageDialog by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    event.collect { event ->
      when (event) {
        NitterEvent.Confirm -> {
          showUsageDialog = false
          if (nitter.text.isEmpty()) {
            nitterVerify = false
            nitterVerifyLoading = false
          } else {
            nitterVerifyLoading = true
            nitterVerify = try {
              nitterRepository.verifyInstance(account.user.screenName, instance = nitter.text)
              true
            } catch (e: Throwable) {
              false
            }
            nitterVerifyLoading = false
          }
          if (nitterVerify) {
            preferencesHolder.miscPreferences.updateData {
              it.copy(nitterInstance = nitter.text)
            }
          }
        }
        is NitterEvent.NitterChanged -> {
          nitter = event.nitter
        }
        NitterEvent.HideInformationDialog -> {
          showInformationDialog = false
        }
        NitterEvent.HideUsageDialog -> {
          showUsageDialog = false
        }
        NitterEvent.ShowInformationDialog -> {
          showInformationDialog = true
        }
        NitterEvent.ShowUsageDialog -> {
          showUsageDialog = true
        }
        NitterEvent.Verify -> {
          if (nitter.text.isEmpty()) {
            nitterVerify = false
            nitterVerifyLoading = false
          } else {
            nitterVerifyLoading = true
            nitterVerify = try {
              nitterRepository.verifyInstance(account.user.screenName, instance = nitter.text)
              true
            } catch (e: Throwable) {
              false
            }
            nitterVerifyLoading = false
          }
        }
      }
    }
  }

  return NitterState(
    nitter = nitter,
    isNitterInputValid = isNitterInputValid,
    nitterVerifyLoading = nitterVerifyLoading,
    nitterVerify = nitterVerify,
    showInformationDialog = showInformationDialog,
    showUsageDialog = showUsageDialog,
  )
}

sealed interface NitterEvent {
  data class NitterChanged(val nitter: TextFieldValue) : NitterEvent
  object Confirm : NitterEvent
  object Verify : NitterEvent
  object ShowInformationDialog : NitterEvent
  object ShowUsageDialog : NitterEvent
  object HideInformationDialog : NitterEvent
  object HideUsageDialog : NitterEvent
}

data class NitterState(
  val nitter: TextFieldValue,
  val isNitterInputValid: Boolean,
  val nitterVerifyLoading: Boolean,
  val nitterVerify: Boolean,
  val showInformationDialog: Boolean,
  val showUsageDialog: Boolean,
)
