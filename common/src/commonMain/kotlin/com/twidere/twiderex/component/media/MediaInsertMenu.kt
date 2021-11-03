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
import com.twidere.twiderex.MR
import com.twidere.twiderex.component.foundation.DropdownMenu
import com.twidere.twiderex.component.foundation.DropdownMenuItem
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.kmp.MediaInsertProvider
import com.twidere.twiderex.model.enums.MediaInsertType
import com.twidere.twiderex.model.ui.UiMediaInsert
import com.twidere.twiderex.navigation.RootRoute
import com.twidere.twiderex.ui.LocalNavController
import kotlinx.coroutines.launch
import moe.tlaster.kfilepicker.FilePicker

private const val VideoSuffix = ".mp4"
private const val ImageSuffix = ".jpg"

enum class MediaLibraryType(
    val extensions: List<String>,
) {
    Video(listOf(".mp4")),
    Image(listOf(".jpg", ".png")),
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MediaInsertMenu(
    modifier: Modifier = Modifier,
    supportMultipleSelect: Boolean = true,
    librariesSupported: Array<MediaLibraryType> = MediaLibraryType.values(),
    disableList: List<MediaInsertType> = emptyList(),
    onResult: (List<UiMediaInsert>) -> Unit
) {
    val navController = LocalNavController.current
    // val storageProvider = get<StorageProvider>()
    val mediaInsertProvider = get<MediaInsertProvider>()
    val scope = rememberCoroutineScope()
    // val filePickerLauncher = if (supportMultipleSelect) rememberLauncherForActivityResult(
    //     contract = ActivityResultContracts.OpenMultipleDocuments(),
    //     onResult = {
    //         scope.launch {
    //             onResult(it.filterNotNull().toUi(mediaInsertProvider))
    //         }
    //     },
    // ) else rememberLauncherForActivityResult(
    //     contract = ActivityResultContracts.OpenDocument(),
    //     onResult = {
    //         scope.launch {
    //             onResult(listOfNotNull(it).toUi(mediaInsertProvider))
    //         }
    //     },
    // )
    //
    // var cameraTempUri by remember {
    //     mutableStateOf(Uri.EMPTY)
    // }
    //
    // val cameraLauncher = rememberLauncherForActivityResult(
    //     contract = ActivityResultContracts.TakePicture(),
    //     onResult = {
    //         scope.launch {
    //             if (it) onResult(listOf(cameraTempUri).toUi(mediaInsertProvider))
    //         }
    //     },
    // )
    //
    // var videoTempUri by remember {
    //     mutableStateOf(Uri.EMPTY)
    // }
    //
    // val videoRecordLauncher = rememberLauncherForActivityResult(
    //     contract = ActivityResultContracts.CaptureVideo(),
    //     onResult = {
    //         scope.launch {
    //             if (it) onResult(listOf(mediaInsertProvider.provideUiMediaInsert(videoTempUri.toString())))
    //         }
    //     },
    // )

    var showDropdown by remember {
        mutableStateOf(false)
    }
    Box(modifier) {

        DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false }, modifier = Modifier) {
            MediaInsertType.values().forEach {
                val enabled = !disableList.contains(it)
                DropdownMenuItem(
                    onClick = {
                        when (it) {
                            MediaInsertType.CAMERA -> {
                                // cameraTempUri = storageProvider.appFiles.mediaFile("${System.currentTimeMillis()}$ImageSuffix").mkFile().toUri(context)
                                // cameraLauncher.launch(cameraTempUri)
                            }
                            MediaInsertType.RECORD_VIDEO -> {
                                // videoTempUri = storageProvider.appFiles.mediaFile("${UUID.randomUUID()}$VideoSuffix").mkFile().toUri(context)
                                // videoRecordLauncher.launch(videoTempUri)
                            }
                            MediaInsertType.LIBRARY -> {
                                scope.launch {
                                    onResult.invoke(
                                        FilePicker.pickFiles(
                                            allowMultiple = supportMultipleSelect,
                                            allowedExtensions = librariesSupported.flatMap { it.extensions }
                                        ).map {
                                            mediaInsertProvider.provideUiMediaInsert(it.path)
                                        }
                                    )
                                }
                                // filePickerLauncher.launch(librariesSupported)
                            }
                            MediaInsertType.GIF -> scope.launch {
                                navController.navigateForResult(RootRoute.Gif.Home)
                                    ?.let { result ->
                                        onResult(
                                            listOf(result as String).map {
                                                mediaInsertProvider.provideUiMediaInsert(
                                                    it
                                                )
                                            }
                                        )
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
                                tint = if (enabled) MaterialTheme.colors.primary else LocalContentColor.current.copy(
                                    alpha = LocalContentAlpha.current
                                )
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
            painter = painterResource(res = MR.files.ic_photo),
            contentDescription = stringResource(
                res = MR.strings.accessibility_scene_compose_image
            )
        )
    }
}

@Composable
private fun MediaInsertType.stringName() = when (this) {
    MediaInsertType.CAMERA -> stringResource(res = MR.strings.accessibility_scene_compose_media_insert_camera)
    MediaInsertType.RECORD_VIDEO -> stringResource(res = MR.strings.accessibility_scene_compose_media_insert_record_video)
    MediaInsertType.LIBRARY -> stringResource(res = MR.strings.accessibility_scene_compose_media_insert_library)
    MediaInsertType.GIF -> stringResource(res = MR.strings.accessibility_scene_compose_media_insert_gif)
}

@Composable
private fun MediaInsertType.icon() = when (this) {
    MediaInsertType.CAMERA -> painterResource(res = MR.files.ic_camera)
    MediaInsertType.RECORD_VIDEO -> painterResource(res = MR.files.ic_video)
    MediaInsertType.LIBRARY -> painterResource(res = MR.files.ic_photo)
    MediaInsertType.GIF -> painterResource(res = MR.files.ic_gif)
}
