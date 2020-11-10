/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
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
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.status.TimelineStatusComponent
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
