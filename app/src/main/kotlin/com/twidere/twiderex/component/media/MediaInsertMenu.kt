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

import android.content.Context
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
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.model.enums.MediaInsertType
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.ui.UiMediaInsert
import com.twidere.twiderex.navigation.RootRoute
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.utils.FileProviderHelper
import kotlinx.coroutines.launch
import java.util.UUID

private const val VideoSuffix = ".mp4"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MediaInsertMenu(
    modifier: Modifier = Modifier,
    supportMultipleSelect: Boolean = true,
    librariesSupported: Array<String> = arrayOf("image/*", "video/*"),
    disableList: List<MediaInsertType> = emptyList(),
    onResult: (List<UiMediaInsert>) -> Unit
) {
    val context = LocalContext.current
    val filePickerLauncher = if (supportMultipleSelect) rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = {
            onResult(it.filterNotNull().toUi(context))
        },
    ) else rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            onResult(listOfNotNull(it).toUi(context))
        },
    )
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()

    var cameraTempUri by remember {
        mutableStateOf(Uri.EMPTY)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) onResult(listOf(cameraTempUri).toUi(context))
        },
    )

    var videoTempUri by remember {
        mutableStateOf(Uri.EMPTY)
    }

    val videoRecordLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = {
            if (it) onResult(listOf(UiMediaInsert(videoTempUri, MediaType.video)))
        },
    )

    var showDropdown by remember {
        mutableStateOf(false)
    }
    Box(modifier) {
        DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false }) {
            MediaInsertType.values().forEach {
                val enabled = !disableList.contains(it)
                DropdownMenuItem(
                    onClick = {
                        when (it) {
                            MediaInsertType.CAMERA -> {
                                cameraTempUri = FileProviderHelper.getUriFromMedias(mediaFileName = UUID.randomUUID().toString(), context)
                                cameraLauncher.launch(cameraTempUri)
                            }
                            MediaInsertType.RECORD_VIDEO -> {
                                videoTempUri = FileProviderHelper.getUriFromMedias(mediaFileName = "${UUID.randomUUID()}$VideoSuffix", context)
                                videoRecordLauncher.launch(videoTempUri)
                            }
                            MediaInsertType.LIBRARY -> filePickerLauncher.launch(librariesSupported)
                            MediaInsertType.GIF -> scope.launch {
                                navController.navigateForResult(RootRoute.Gif.Home)
                                    ?.let { result ->
                                        onResult(listOf(result as Uri).toUi(context))
                                    }
                            }
                        }
                        showDropdown = false
                    },
                    enabled = enabled
                ) {
                    ListItem(
                        text = {
                            Text(text = it.stringName())
                        },
                        icon = {
                            Icon(
                                painter = it.icon(),
                                contentDescription = it.stringName(),
                                tint = if (enabled) MaterialTheme.colors.primary else LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
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

private fun List<Uri>.toUi(context: Context) = map {
    val mimeType = context.contentResolver.getType(it) ?: "image/*"
    UiMediaInsert(
        uri = it,
        type = when {
            mimeType.startsWith("video") -> MediaType.video
            mimeType == "image/gif" -> MediaType.animated_gif
            else -> MediaType.photo
        }
    )
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
