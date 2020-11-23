package com.twidere.twiderex.scenes.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.zoomable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonConstants
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.navigate
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.navigation.AmbientNavigator
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.AmbientActiveAccountViewModel
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.ui.statusActionIconSize

@Composable
fun AccountManagementScene() {
    TwidereXTheme {
        Scaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(id = R.string.title_accounts))
                    },
                    actions = {
                        val navController = AmbientNavController.current
                        IconButton(onClick = {
                            navController.navigate(Route.SignIn.Twitter)
                        }) {
                            Icon(asset = Icons.Default.Add)
                        }
                    }
                )
            }
        ) {
            val activeAccountViewModel = AmbientActiveAccountViewModel.current
            val accounts by activeAccountViewModel.allAccounts.observeAsState(initial = emptyList())
            LazyColumnFor(items = accounts) { detail ->
                detail.user.toUi().let {
                    ListItem(
                        icon = {
                            UserAvatar(
                                user = it,
                            )
                        },
                        text = {
                            Text(
                                text = it.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        secondaryText = {
                            Text(
                                text = "@${it.screenName}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        trailing = {
                            var expanded by remember { mutableStateOf(false) }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                toggle = {
                                    IconButton(
                                        onClick = {
                                            expanded = true
                                        },
                                    ) {
                                Icon(asset = Icons.Default.MoreVert)
                                    }
                                },
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        activeAccountViewModel.deleteAccount(detail)
                                    },
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.action_remove),
                                        color = Color.Red,
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}