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
package com.twidere.twiderex.scenes.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.twidere.twiderex.component.foundation.AlertDialog
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.lazy.ItemHeader
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.settings.RadioItem
import com.twidere.twiderex.component.settings.switchItem
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.preferences.model.MiscPreferences
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.settings.MiscViewModel

@Composable
fun MiscScene() {
    val viewModel: MiscViewModel = getViewModel()

    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_title))
                    }
                )
            }
        ) {
            val showProxyTypeDialog = remember { mutableStateOf(false) }
            val proxyTypeValue by viewModel.proxyType.observeAsState(initial = MiscPreferences.ProxyType.HTTP)
            val showProxyInputDialog = remember { mutableStateOf(false) }
            val inputTitle = remember {
                mutableStateOf("")
            }
            val inputValue = remember {
                mutableStateOf("")
            }
            val inputChanged = remember {
                mutableStateOf<(value: String) -> Unit>({})
            }

            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .verticalScroll(
                            rememberScrollState()
                        )
                ) {
                    NitterPreference(viewModel)
                    ProxyPreference(
                        viewModel = viewModel,
                        showProxyInputDialog = showProxyInputDialog,
                        showProxyTypeDialog = showProxyTypeDialog,
                        inputTitle = inputTitle,
                        inputValue = inputValue,
                        inputChanged = inputChanged
                    )
                }

                if (showProxyTypeDialog.value) {
                    ProxyTypeSelectDialog(
                        onDismissRequest = { showProxyTypeDialog.value = false },
                        onSelect = {
                            viewModel.setProxyType(it.name)
                        },
                        value = proxyTypeValue
                    )
                }
                if (showProxyInputDialog.value) {
                    ProxyInputDialog(
                        title = inputTitle.value,
                        value = inputValue.value,
                        onValueChanged = inputChanged.value,
                        onDismissRequest = {
                            showProxyInputDialog.value = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ColumnScope.ProxyPreference(
    viewModel: MiscViewModel,
    showProxyInputDialog: MutableState<Boolean>,
    showProxyTypeDialog: MutableState<Boolean>,
    inputTitle: MutableState<String>,
    inputValue: MutableState<String>,
    inputChanged: MutableState<(value: String) -> Unit>
) {
    val useProxy by viewModel.useProxy.observeAsState(false)
    val proxyType by viewModel.proxyType.observeAsState(MiscPreferences.ProxyType.HTTP)
    val proxyServer by viewModel.proxyServer.observeAsState("")
    val proxyPort by viewModel.proxyPort.observeAsState(null)
    val proxyUserName by viewModel.proxyUserName.observeAsState("")
    val proxyPassword by viewModel.proxyPassword.observeAsState("")

    ItemHeader {
        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_proxy_title))
    }
    switchItem(
        value = useProxy,
        onChanged = {
            viewModel.setUseProxy(it)
        },
        describe = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_proxy_enable_description))
        }
    ) {
        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_proxy_enable_title))
    }
    ItemProxy(
        enable = useProxy,
        title = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_proxy_type_title),
        content = proxyTypeValue(type = proxyType),
        onClick = {
            showProxyTypeDialog.value = true
        }
    )

    val serverTitle = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_proxy_server)
    ItemProxy(
        enable = useProxy,
        title = serverTitle,
        content = proxyServer,
        onClick = {
            inputTitle.value = serverTitle
            inputValue.value = proxyServer
            inputChanged.value = {
                viewModel.setProxyServer(it)
            }
            showProxyInputDialog.value = true
        }
    )

    val portTitle = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_proxy_port_title)
    ItemProxy(
        enable = useProxy,
        title = portTitle,
        content = proxyPort?.toString() ?: "",
        onClick = {
            inputTitle.value = portTitle
            inputValue.value = proxyPort?.toString() ?: ""
            inputChanged.value = {
                it.toIntOrNull()?.let {
                    viewModel.setProxyPort(it)
                } ?: run {
                    // TODO: show error
                }
            }
            showProxyInputDialog.value = true
        }
    )

    val userNameTitle = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_proxy_username)
    ItemProxy(
        enable = useProxy,
        title = userNameTitle,
        content = proxyUserName,
        onClick = {
            inputTitle.value = userNameTitle
            inputValue.value = proxyUserName
            inputChanged.value = {
                viewModel.setProxyUserName(it)
            }
            showProxyInputDialog.value = true
        }
    )

    val passwordTitle = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_proxy_password)
    ItemProxy(
        enable = useProxy,
        title = passwordTitle,
        content = proxyPassword,
        onClick = {
            inputTitle.value = passwordTitle
            inputValue.value = proxyPassword
            inputChanged.value = {
                viewModel.setProxyPassword(it)
            }
            showProxyInputDialog.value = true
        }
    )
}

@Composable
fun proxyTypeValue(type: MiscPreferences.ProxyType): String {
    return when (type) {
        MiscPreferences.ProxyType.HTTP -> stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_proxy_type_http)
        MiscPreferences.ProxyType.REVERSE -> stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_proxy_type_reverse)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemProxy(
    enable: Boolean,
    title: String,
    content: String,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(
            onClick = {
                onClick.invoke()
            },
            enabled = enable
        ),
        text = {
            CompositionLocalProvider(LocalContentAlpha provides if (enable) ContentAlpha.high else ContentAlpha.disabled) {
                Text(text = title)
            }
        },
        secondaryText = {
            CompositionLocalProvider(LocalContentAlpha provides if (enable) ContentAlpha.medium else ContentAlpha.disabled) {
                Text(text = content)
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NitterPreference(viewModel: MiscViewModel) {
    val value by viewModel.nitter.observeAsState(initial = "")
    var showInformationDialog by remember {
        mutableStateOf(false)
    }
    var showUsageDialog by remember {
        mutableStateOf(false)
    }
    if (showUsageDialog) {
        NitterUsageDialog(
            onDismissRequest = {
                showUsageDialog = false
            }
        )
    }
    if (showInformationDialog) {
        NitterInformationDialog(
            onDismissRequest = {
                showInformationDialog = false
            }
        )
    }
    ItemHeader(
        trailing = {
            IconButton(
                onClick = {
                    showInformationDialog = true
                }
            ) {
                Icon(
                    painter = painterResource(res = com.twidere.twiderex.MR.files.ic_info_circle),
                    contentDescription = null,
                )
            }
        }
    ) {
        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_nitter_title))
    }
    ListItem(
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { viewModel.setNitterInstance(it) },
                placeholder = {
                    Text(
                        text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_nitter_input_placeholder)
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            showUsageDialog = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(res = com.twidere.twiderex.MR.files.ic_info_circle),
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        secondaryText = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_nitter_input_description))
        }
    )
}

@Composable
fun NitterUsageDialog(
    onDismissRequest: () -> Unit,
) {
    val navigator = LocalNavigator.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_nitter_dialog_usage_title))
        },
        text = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_nitter_dialog_usage_content))
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    navigator.openLink(
                        "https://github.com/zedeus/nitter",
                        deepLink = false
                    )
                }
            ) {
                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_nitter_dialog_usage_project_button))
            }
        }
    )
}

@Composable
fun NitterInformationDialog(
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            onDismissRequest.invoke()
        },
        title = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_nitter_dialog_information_title))
        },
        text = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_nitter_dialog_information_content))
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_ok))
            }
        }
    )
}

@Composable
fun ProxyTypeSelectDialog(
    onDismissRequest: () -> Unit,
    onSelect: (value: MiscPreferences.ProxyType) -> Unit,
    value: MiscPreferences.ProxyType
) {
    var selected by remember {
        mutableStateOf(value)
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_misc_proxy_type_title))
        },
        text = {
            Column {
                RadioItem(
                    options = listOf(
                        MiscPreferences.ProxyType.HTTP,
                        MiscPreferences.ProxyType.REVERSE,
                    ),
                    value = selected,
                    onChanged = {
                        selected = it
                    },
                    title = {},
                    itemContent = {
                        Text(
                            text = proxyTypeValue(type = it)
                        )
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSelect.invoke(selected)
                    onDismissRequest.invoke()
                }
            ) {
                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_ok))
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProxyInputDialog(
    title: String,
    value: String,
    onValueChanged: (value: String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var input by remember {
        mutableStateOf(value)
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = title)
        },
        text = {
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onValueChanged(input)
                    onDismissRequest.invoke()
                }
            ) {
                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_ok))
            }
        }
    )
}
