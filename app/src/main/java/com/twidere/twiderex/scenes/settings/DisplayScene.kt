package com.twidere.twiderex.scenes.settings

import androidx.compose.foundation.Text
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.TimelineStatusComponent
import com.twidere.twiderex.component.lazy.itemDivider
import com.twidere.twiderex.component.lazy.itemHeader
import com.twidere.twiderex.component.settings.radioItem
import com.twidere.twiderex.component.settings.switchItem
import com.twidere.twiderex.extensions.navViewModel
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.settings.AmbientFontScale
import com.twidere.twiderex.settings.AmbientUseSystemFontSize
import com.twidere.twiderex.ui.AmbientInStoryboard
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.viewmodel.settings.DisplayViewModel

@OptIn(ExperimentalLazyDsl::class)
@Composable
fun DisplayScene() {
    val viewModel = navViewModel<DisplayViewModel>()
    val useSystemFontSize = AmbientUseSystemFontSize.current
    val fontScale = AmbientFontScale.current
    TwidereXTheme {
        Scaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = "Display")
                    }
                )
            }
        ) {
            LazyColumn {
                itemHeader {
                    Text(text = "PREVIEW")
                }
                item {
                    Providers(
                        AmbientInStoryboard provides true
                    ) {
                        TimelineStatusComponent(data = UiStatus.sample())
                    }
                }
                itemDivider()
                itemHeader {
                    Text(text = "TEXT")
                }
                switchItem(viewModel.useSystemFontSizeSettings)
                if (!useSystemFontSize) {
                    item {
                        ListItem(
                            icon = {
                                Icon(asset = Icons.Default.TextFields)
                            },
                            text = {
                                Slider(
                                    value = fontScale,
                                    onValueChange = { viewModel.fontScaleSettings.apply(it) },
                                    valueRange = 0.1f..2f
                                )
                            },
                            trailing = {
                                Icon(asset = Icons.Default.TextFields)
                            }
                        )
                    }
                }
                itemDivider()
                radioItem(viewModel.avatarStyleSettings)
                itemDivider()
                itemHeader {
                    Text(text = "MEDIA")
                }
                switchItem(viewModel.mediaPreviewSettings)
            }
        }
    }
}