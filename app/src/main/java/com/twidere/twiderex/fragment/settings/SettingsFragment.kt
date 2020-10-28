package com.twidere.twiderex.fragment.settings

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import com.twidere.twiderex.R
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.extensions.NavControllerAmbient
import com.twidere.twiderex.fragment.JetFragment

data class SettingItem(
    val name: String,
    val icon: VectorAsset,
    val target: Int,
)

class SettingsFragment : JetFragment() {
    private val settings by lazy {
        mapOf(
            "GENERAL" to listOf(
                SettingItem(
                    "Appearance",
                    Icons.Default.Home,
                    target = R.id.settings_appearance_fragment,
                ),
                SettingItem(
                    "Display",
                    Icons.Default.Home,
                    target = 0,
                ),
                SettingItem(
                    "Layout",
                    Icons.Default.Home,
                    target = 0,
                ),
                SettingItem(
                    "Web Browser",
                    Icons.Default.Home,
                    target = 0,
                ),
            ),
            "ABOUT" to listOf(
                SettingItem(
                    "About",
                    Icons.Default.Info,
                    target = 0,
                ),
            )
        )
    }

    @OptIn(ExperimentalLazyDsl::class)
    @Composable
    override fun onCompose() {
        Scaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = "Settings")
                    }
                )
            }
        ) {
            LazyColumn(
                contentPadding = it
            ) {
                settings.forEach {
                    item {
                        ListItem(
                            text = {
                                ProvideTextStyle(value = MaterialTheme.typography.button) {
                                    Text(text = it.key)
                                }
                            },
                        )
                    }
                    items(it.value) {
                        val navController = NavControllerAmbient.current
                        ListItem(
                            modifier = Modifier.clickable(onClick = {
                                if (it.target != 0) {
                                    navController.navigate(it.target)
                                }
                            }),
                            icon = {
                                Icon(asset = it.icon)
                            },
                            text = {
                                Text(text = it.name)
                            },
                        )
                    }
                }
            }
        }
    }
}