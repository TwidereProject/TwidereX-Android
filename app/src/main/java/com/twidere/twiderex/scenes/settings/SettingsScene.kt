package com.twidere.twiderex.scenes.settings

import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.navigation.compose.navigate
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.ui.AmbientNavController


data class SettingItem(
    val name: String,
    val icon: VectorAsset,
    val route: String,
)

private val settings by lazy {
    mapOf(
        "GENERAL" to listOf(
            SettingItem(
                "Appearance",
                Icons.Default.Home,
                route = "settings/appearance",
            ),
            SettingItem(
                "Display",
                Icons.Default.Home,
                route = "",
            ),
            SettingItem(
                "Layout",
                Icons.Default.Home,
                route = "",
            ),
            SettingItem(
                "Web Browser",
                Icons.Default.Home,
                route = "",
            ),
        ),
        "ABOUT" to listOf(
            SettingItem(
                "About",
                Icons.Default.Info,
                route = "",
            ),
        )
    )
}

@OptIn(ExperimentalLazyDsl::class)
@Composable
fun SettingsScene() {

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
                    val navController = AmbientNavController.current
                    ListItem(
                        modifier = Modifier.clickable(
                            onClick = {
                                if (it.route.isNotEmpty()) {
                                    navController.navigate(it.route)
                                }
                            }
                        ),
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