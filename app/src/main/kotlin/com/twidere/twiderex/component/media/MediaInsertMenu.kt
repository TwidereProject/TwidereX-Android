/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.component.media

import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.model.enums.MediaInsertType

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MediaInsertMenu(
    modifier: Modifier = Modifier,
    disableList: List<MediaInsertType> = emptyList(),
    onSelect: (MediaInsertType) -> Unit
) {
    var showDropdown by remember {
        mutableStateOf(false)
    }
    Box(modifier) {
        DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false }) {
            MediaInsertType.values().forEach {
                if (!disableList.contains(it)) {
                    DropdownMenuItem(
                        onClick = {
                            onSelect(it)
                            showDropdown = false
                        }
                    ) {
                        ListItem(
                            text = {
                                Text(text = it.stringName())
                            },
                            icon = {
                                Icon(
                                    painter = it.icon(),
                                    contentDescription = it.stringName(),
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        )
                    }
                }
            }
        }
        IconButton(
            onClick = {
                showDropdown = !showDropdown
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_photo),
                contentDescription = stringResource(
                    id = R.string.accessibility_scene_compose_image
                )
            )
        }
    }
}

@Composable
private fun MediaInsertType.stringName() = when (this) {
    MediaInsertType.CAMERA -> "Take Photo"
    MediaInsertType.RECORD_VIDEO -> "Record Video"
    MediaInsertType.LIBRARY -> "Browse Library"
    MediaInsertType.GIF -> "Add GIF"
}

@Composable
private fun MediaInsertType.icon() = when (this) {
    MediaInsertType.CAMERA -> painterResource(id = R.drawable.ic_camera)
    MediaInsertType.RECORD_VIDEO -> painterResource(id = R.drawable.ic_video)
    MediaInsertType.LIBRARY -> painterResource(id = R.drawable.ic_photo)
    MediaInsertType.GIF -> painterResource(id = R.drawable.ic_gif)
}
