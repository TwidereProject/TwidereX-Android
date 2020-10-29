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
package com.twidere.twiderex.settings

import android.content.SharedPreferences
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.core.content.edit
import com.twidere.twiderex.settings.types.SettingItem
import com.twidere.twiderex.ui.blue
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding

class PrimaryColorSetting(
    private val preferences: SharedPreferences,
) : SettingItem<Color>() {

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun save(value: Color) {
        preferences.edit {
            putLong(key, value.value.toLong())
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun load(): Color {
        return Color(preferences.getLong(key, blue.value.toLong()))
    }
}

val AmbientPrimaryColor = ambientOf<PrimaryColorSetting>()

@Composable
fun primaryColorDialog(onDismiss: () -> Unit) {
    val setting = AmbientPrimaryColor.current
    val current by setting.data.observeAsState(initial = setting.initialValue)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Select color")
        },
        text = {
            LazyRowFor(
                items = listOf(
                    Color.Blue,
                    Color.Cyan,
                    Color.DarkGray,
                    Color.Gray,
                    Color.LightGray,
                    Color.Green,
                    Color.Magenta,
                    Color.Red,
                    Color.Yellow,
                )
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = standardPadding)
                ) {
                    Box(
                        modifier = Modifier
                            .size(profileImageSize)
                            .clip(CircleShape)
                            .background(it)
                            .clickable(
                                onClick = {
                                    setting.apply(it)
                                }
                            ),
                        alignment = Alignment.Center,
                    ) {
                        if (current == it) {
                            Checkbox(checked = true, onCheckedChange = {})
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CLOSE")
            }
        }
    )
}
