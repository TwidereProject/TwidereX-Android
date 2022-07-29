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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.extensions.collectEvent
import com.twidere.twiderex.extensions.rememberNestedPresenter
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.preferences.model.MiscPreferences
import com.twidere.twiderex.repository.NitterRepository
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun MiscPresenter(
  flow: Flow<MiscEvent>,
): MiscState {
  val account = CurrentAccountPresenter()
  if (account !is CurrentAccountState.Account) {
    return MiscState.NoAccount
  }
  val user = remember(account.account) {
    account.account.toUi()
  }
  val (nitterState, nitterChannel) = rememberNestedPresenter {
    NitterPresenter(
      it,
      account.account
    )
  }
  val (proxyState, proxyChannel) = rememberNestedPresenter { ProxyPresenter(it) }

  flow.collectEvent {
    when (this) {
      is MiscEvent.Nitter -> nitterChannel.trySend(event)
      is MiscEvent.Proxy -> proxyChannel.trySend(event)
    }
  }

  return MiscState.State(
    user = user,
    nitterState = nitterState,
    proxyState = proxyState,
  )
}

interface MiscState {
  object NoAccount : MiscState
  data class State(
    val user: UiUser,
    val nitterState: NitterState,
    val proxyState: ProxyState,
  ) : MiscState
}

interface MiscEvent {
  data class Proxy(val event: ProxyEvent) : MiscEvent
  data class Nitter(val event: NitterEvent) : MiscEvent
}

@Composable
fun ProxyPresenter(
  flow: Flow<ProxyEvent>,
  preferencesHolder: PreferencesHolder = get(),
): ProxyState {
  val preferences by preferencesHolder.miscPreferences.data.collectAsState(MiscPreferences())

  val (proxyTypeState, proxyTypeChannel) = rememberNestedPresenter { ProxyTypePresenter(it) }
  val (proxyHostState, proxyHostChannel) = rememberNestedPresenter { ProxyHostPresenter(it) }
  val (proxyPortState, proxyPortChannel) = rememberNestedPresenter { ProxyPortPresenter(it) }
  val (proxyUserNameState, proxyUserNameChannel) = rememberNestedPresenter {
    ProxyUserNamePresenter(
      it
    )
  }
  val (proxyPasswordState, proxyPasswordChannel) = rememberNestedPresenter {
    ProxyPasswordPresenter(
      it
    )
  }

  flow.collectEvent {
    when (this) {
      is ProxyEvent.ProxyEnabledChanged -> preferencesHolder.miscPreferences.updateData {
        it.copy(useProxy = enabled)
      }
      is ProxyEvent.ProxyHost -> proxyHostChannel.trySend(event)
      is ProxyEvent.ProxyPassword -> proxyPasswordChannel.trySend(event)
      is ProxyEvent.ProxyPort -> proxyPortChannel.trySend(event)
      is ProxyEvent.ProxyType -> proxyTypeChannel.trySend(event)
      is ProxyEvent.ProxyUserName -> proxyUserNameChannel.trySend(event)
    }
  }

  return ProxyState(
    proxyHost = preferences.proxyServer,
    proxyPort = preferences.proxyPort,
    proxyType = preferences.proxyType,
    proxyEnabled = preferences.useProxy,
    proxyUserName = preferences.proxyUserName,
    proxyPassword = preferences.proxyPassword,
    proxyHostState = proxyHostState,
    proxyPortState = proxyPortState,
    proxyTypeState = proxyTypeState,
    proxyUserNameState = proxyUserNameState,
    proxyPasswordState = proxyPasswordState,
  )
}

data class ProxyState(
  val proxyEnabled: Boolean,
  val proxyType: MiscPreferences.ProxyType,
  val proxyHost: String,
  val proxyPort: Int,
  val proxyUserName: String,
  val proxyPassword: String,
  val proxyHostState: ProxyHostState,
  val proxyPortState: ProxyPortState,
  val proxyTypeState: ProxyTypeState,
  val proxyUserNameState: ProxyUserNameState,
  val proxyPasswordState: ProxyPasswordState,
)

interface ProxyEvent {
  data class ProxyEnabledChanged(val enabled: Boolean) : ProxyEvent
  data class ProxyType(val event: ProxyTypeEvent) : ProxyEvent
  data class ProxyHost(val event: ProxyHostEvent) : ProxyEvent
  data class ProxyPort(val event: ProxyPortEvent) : ProxyEvent
  data class ProxyUserName(val event: ProxyUserNameEvent) : ProxyEvent
  data class ProxyPassword(val event: ProxyPasswordEvent) : ProxyEvent
}

@Composable
fun ProxyHostPresenter(
  event: Flow<ProxyHostEvent>,
  preferencesHolder: PreferencesHolder = get(),
): ProxyHostState {
  var proxyHost by remember {
    mutableStateOf(
      TextFieldValue()
    )
  }
  var showDialog by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    preferencesHolder.miscPreferences.data.firstOrNull()?.let {
      proxyHost = TextFieldValue(it.proxyServer, selection = TextRange(it.proxyServer.length))
    }
  }
  event.collectEvent {
    when (this) {
      is ProxyHostEvent.ShowDialog -> showDialog = show
      is ProxyHostEvent.HostChanged -> proxyHost = host
      is ProxyHostEvent.Save -> {
        preferencesHolder.miscPreferences.updateData {
          it.copy(proxyServer = proxyHost.text)
        }
        showDialog = false
      }
    }
  }
  return ProxyHostState(
    host = proxyHost,
    showDialog = showDialog,
  )
}

data class ProxyHostState(
  val showDialog: Boolean,
  val host: TextFieldValue,
)

interface ProxyHostEvent {
  data class HostChanged(val host: TextFieldValue) : ProxyHostEvent
  data class ShowDialog(val show: Boolean) : ProxyHostEvent
  object Save : ProxyHostEvent
}

@Composable
fun ProxyPortPresenter(
  event: Flow<ProxyPortEvent>,
  preferencesHolder: PreferencesHolder = get(),
): ProxyPortState {
  var proxyPort by remember {
    mutableStateOf(
      TextFieldValue()
    )
  }
  var showDialog by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    preferencesHolder.miscPreferences.data.firstOrNull()?.let {
      proxyPort = TextFieldValue(it.proxyPort.toString(), selection = TextRange(it.proxyPort.toString().length))
    }
  }
  event.collectEvent {
    when (this) {
      is ProxyPortEvent.ShowDialog -> showDialog = show
      is ProxyPortEvent.PortChanged -> proxyPort = port
      is ProxyPortEvent.Save -> {
        preferencesHolder.miscPreferences.updateData {
          it.copy(proxyPort = proxyPort.text.toInt())
        }
        showDialog = false
      }
    }
  }
  return ProxyPortState(
    showDialog = showDialog,
    port = proxyPort,
  )
}

data class ProxyPortState(
  val showDialog: Boolean,
  val port: TextFieldValue,
)

interface ProxyPortEvent {
  data class PortChanged(val port: TextFieldValue) : ProxyPortEvent
  data class ShowDialog(val show: Boolean) : ProxyPortEvent
  object Save : ProxyPortEvent
}

@Composable
fun ProxyUserNamePresenter(
  event: Flow<ProxyUserNameEvent>,
  preferencesHolder: PreferencesHolder = get(),
): ProxyUserNameState {
  var proxyUserName by remember {
    mutableStateOf(
      TextFieldValue()
    )
  }
  var showDialog by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    preferencesHolder.miscPreferences.data.firstOrNull()?.let {
      proxyUserName = TextFieldValue(it.proxyUserName, selection = TextRange(it.proxyUserName.length))
    }
  }
  event.collectEvent {
    when (this) {
      is ProxyUserNameEvent.ShowDialog -> showDialog = show
      is ProxyUserNameEvent.UserNameChanged -> proxyUserName = userName
      is ProxyUserNameEvent.Save -> {
        preferencesHolder.miscPreferences.updateData {
          it.copy(proxyUserName = proxyUserName.text)
        }
        showDialog = false
      }
    }
  }
  return ProxyUserNameState(
    showDialog = showDialog,
    userName = proxyUserName,
  )
}

data class ProxyUserNameState(
  val showDialog: Boolean,
  val userName: TextFieldValue,
)

interface ProxyUserNameEvent {
  data class UserNameChanged(val userName: TextFieldValue) : ProxyUserNameEvent
  data class ShowDialog(val show: Boolean) : ProxyUserNameEvent
  object Save : ProxyUserNameEvent
}

@Composable
fun ProxyPasswordPresenter(
  flow: Flow<ProxyPasswordEvent>,
  preferencesHolder: PreferencesHolder = get(),
): ProxyPasswordState {
  var proxyPassword by remember {
    mutableStateOf(
      TextFieldValue()
    )
  }
  var showDialog by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    preferencesHolder.miscPreferences.data.firstOrNull()?.let {
      proxyPassword = TextFieldValue(it.proxyPassword, selection = TextRange(it.proxyPassword.length))
    }
  }
  flow.collectEvent {
    when (this) {
      is ProxyPasswordEvent.ShowDialog -> showDialog = show
      is ProxyPasswordEvent.PasswordChanged -> proxyPassword = password
      is ProxyPasswordEvent.Save -> {
        preferencesHolder.miscPreferences.updateData {
          it.copy(proxyPassword = proxyPassword.text)
        }
        showDialog = false
      }
    }
  }
  return ProxyPasswordState(
    showDialog = showDialog,
    password = proxyPassword,
  )
}

data class ProxyPasswordState(
  val showDialog: Boolean,
  val password: TextFieldValue,
)

interface ProxyPasswordEvent {
  data class PasswordChanged(val password: TextFieldValue) : ProxyPasswordEvent
  data class ShowDialog(val show: Boolean) : ProxyPasswordEvent
  object Save : ProxyPasswordEvent
}

@Composable
fun ProxyTypePresenter(
  event: Flow<ProxyTypeEvent>,
  preferencesHolder: PreferencesHolder = get(),
): ProxyTypeState {
  var proxyType by remember { mutableStateOf(MiscPreferences.ProxyType.HTTP) }
  var showDialog by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    preferencesHolder.miscPreferences.data.firstOrNull()?.let {
      proxyType = it.proxyType
    }
  }
  event.collectEvent {
    when (this) {
      is ProxyTypeEvent.ShowDialog -> showDialog = show
      is ProxyTypeEvent.TypeChanged -> proxyType = type
      is ProxyTypeEvent.Save -> {
        preferencesHolder.miscPreferences.updateData {
          it.copy(proxyType = proxyType)
        }
        showDialog = false
      }
    }
  }
  return ProxyTypeState(
    showDialog = showDialog,
    type = proxyType,
  )
}

data class ProxyTypeState(
  val showDialog: Boolean,
  val type: MiscPreferences.ProxyType,
)

interface ProxyTypeEvent {
  data class TypeChanged(val type: MiscPreferences.ProxyType) : ProxyTypeEvent
  data class ShowDialog(val show: Boolean) : ProxyTypeEvent
  object Save : ProxyTypeEvent
}

@Composable
fun NitterPresenter(
  event: Flow<NitterEvent>,
  account: AccountDetails,
  nitterRepository: NitterRepository = get(),
  preferencesHolder: PreferencesHolder = get(),
): NitterState {
  var nitter by remember {
    mutableStateOf(
      TextFieldValue()
    )
  }
  LaunchedEffect(Unit) {
    preferencesHolder.miscPreferences.data.firstOrNull()?.let {
      nitter = TextFieldValue(it.nitterInstance, selection = TextRange(it.nitterInstance.length))
    }
  }
  val isNitterInputValid =
    (nitter.text.startsWith("https://") || nitter.text.startsWith("http://")) && !nitter.text.endsWith(
      "/"
    )
  var nitterVerifyLoading by remember { mutableStateOf(false) }
  var nitterVerify by remember { mutableStateOf(false) }

  var showInformationDialog by remember { mutableStateOf(false) }
  var showUsageDialog by remember { mutableStateOf(false) }

  event.collectEvent {
    when (this) {
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
        nitter = this.nitter
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

  return NitterState(
    nitter = nitter,
    isNitterInputValid = isNitterInputValid,
    nitterVerifyLoading = nitterVerifyLoading,
    nitterVerify = nitterVerify,
    showInformationDialog = showInformationDialog,
    showUsageDialog = showUsageDialog,
  )
}

interface NitterEvent {
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
