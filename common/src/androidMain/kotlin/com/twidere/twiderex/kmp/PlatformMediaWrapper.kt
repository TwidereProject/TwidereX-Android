/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
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
package com.twidere.twiderex.kmp

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.extensions.toUri
import com.twidere.twiderex.model.ui.UiMediaInsert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

private const val VideoSuffix = ".mp4"
private const val ImageSuffix = ".jpg"

@Composable
actual fun PlatformMediaWrapper(
    scope: CoroutineScope,
    onResult: (List<UiMediaInsert>) -> Unit,
    content: @Composable (launchCamera: () -> Unit, launchVideo: () -> Unit) -> Unit
) {

    var cameraTempUri by remember {
        mutableStateOf(Uri.EMPTY)
    }

    var videoTempUri by remember {
        mutableStateOf(Uri.EMPTY)
    }

    val mediaInsertProvider = get<MediaInsertProvider>()

    val storageProvider = get<StorageProvider>()

    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            scope.launch {
                if (it) onResult(listOf(cameraTempUri).toUi(mediaInsertProvider))
            }
        },
    )

    val videoRecordLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = {
            scope.launch {
                if (it) onResult(listOf(mediaInsertProvider.provideUiMediaInsert(videoTempUri.toString())))
            }
        },
    )

    val launchCamera = remember {
        {
            cameraTempUri =
                storageProvider.appFiles.mediaFile("${System.currentTimeMillis()}$ImageSuffix")
                    .mkFile().toUri(context)
            cameraLauncher.launch(cameraTempUri)
        }
    }

    val launchVideo = remember {
        {
            videoTempUri =
                storageProvider.appFiles.mediaFile("${UUID.randomUUID()}$VideoSuffix").mkFile()
                    .toUri(context)
            videoRecordLauncher.launch(videoTempUri)
        }
    }
    content.invoke(launchCamera, launchVideo)
}

private suspend fun List<Uri>.toUi(mediaInsertProvider: MediaInsertProvider) = map {
    mediaInsertProvider.provideUiMediaInsert(it.toString())
}
