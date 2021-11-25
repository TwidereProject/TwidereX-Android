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
package com.twidere.twiderex

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.twidere.twiderex.media.JFXMediaPlayer

// @ExperimentalComposeUiApi
// fun main(args: Array<String>) {
//     DesktopMediaPlayerHelper.register(DesktopMediaPlayerFactoryImpl())
//     runDesktopApp(args)
// }

fun main() {
    application {
        Window(
            onCloseRequest = { },
            title = "TwidereX",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
            ),
        ) {
            val showVideo = remember { mutableStateOf(false) }
            if (showVideo.value) {
                val mediaPlayer = remember {
                    JFXMediaPlayer("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
                }
                Column {
                    mediaPlayer.Content(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    ) {
                    }
                    Row {
                        Button(onClick = {
                            mediaPlayer.play()
                        }) {
                            Text("Play")
                        }

                        Button(onClick = {
                            mediaPlayer.pause()
                        }) {
                            Text("Pause")
                        }

                        Button(onClick = {
                            mediaPlayer.release()
                            showVideo.value = false
                        }) {
                            Text("release")
                        }
                    }
                }
            } else {
                Button(onClick = { showVideo.value = true }) {
                    Text("open Video")
                }
            }
        }
    }
}
