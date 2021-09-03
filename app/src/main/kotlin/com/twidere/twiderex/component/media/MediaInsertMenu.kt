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

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.model.enums.MediaInsertType
import com.twidere.twiderex.utils.FileProviderHelper
import java.util.UUID

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MediaInsertMenu(
    modifier: Modifier = Modifier,
    disableList: List<MediaInsertType> = emptyList(),
    onResult: (List<Uri>) -> Unit
) {
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = {
            onResult(it)
        },
    )

    var cameraTempUri by remember {
        mutableStateOf(Uri.EMPTY)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) onResult(listOf(cameraTempUri))
        },
    )

    var videoTempUri by remember {
        mutableStateOf(Uri.EMPTY)
    }

    val videoRecordLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = {
            if (it) onResult(listOf(videoTempUri))
        },
    )

    var showDropdown by remember {
        mutableStateOf(false)
    }
    Box(modifier) {
        DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false }) {
            MediaInsertType.values().forEach {
                if (!disableList.contains(it)) {
                    DropdownMenuItem(
                        onClick = {
                            when (it) {
                                MediaInsertType.CAMERA -> {
                                    cameraTempUri = FileProviderHelper.getUriFromMedias(mediaFileName = UUID.randomUUID().toString(), context)
                                    cameraLauncher.launch(cameraTempUri)
                                }
                                MediaInsertType.RECORD_VIDEO -> {
                                    videoTempUri = FileProviderHelper.getUriFromMedias(mediaFileName = UUID.randomUUID().toString(), context)
                                    videoRecordLauncher.launch(videoTempUri)
                                }
                                MediaInsertType.LIBRARY -> filePickerLauncher.launch(arrayOf("image/*", "video/*"))
                                MediaInsertType.GIF -> TODO()
                            }
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
