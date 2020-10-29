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
package com.twidere.twiderex.fragment.settings

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.lazy.itemDivider
import com.twidere.twiderex.component.settings.radioItem
import com.twidere.twiderex.fragment.JetFragment
import com.twidere.twiderex.settings.AmbientTabPosition
import com.twidere.twiderex.settings.AmbientTheme
import com.twidere.twiderex.settings.primaryColorDialog

class AppearanceFragment : JetFragment() {
    @OptIn(ExperimentalLazyDsl::class)
    @Composable
    override fun onCompose() {
        val tabPosition = AmbientTabPosition.current
        val theme = AmbientTheme.current
        var showPrimaryColorDialog by remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = "Appearance")
                    },
                )
            }
        ) {
            if (showPrimaryColorDialog) {
                primaryColorDialog(
                    onDismiss = {
                        showPrimaryColorDialog = false
                    }
                )
            }
            LazyColumn {
                item {
                    ListItem(
                        modifier = Modifier.clickable(
                            onClick = {
                                showPrimaryColorDialog = true
                            }
                        ),
                        text = {
                            Text(text = "Highlight color")
                        },
                        trailing = {
                            Box(
                                modifier = Modifier
                                    .preferredHeight(24.dp)
                                    .preferredWidth(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .aspectRatio(1F)
                                    .background(MaterialTheme.colors.primary),
                            ) {
                            }
                        }
                    )
                }
                itemDivider()
                radioItem(theme)
                itemDivider()
                radioItem(tabPosition)
            }
        }
    }
}
