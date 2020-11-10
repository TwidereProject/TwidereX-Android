/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.settings

import android.content.SharedPreferences
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRowForIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ambientOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.core.content.edit
import com.twidere.twiderex.extensions.isDarkTheme
import com.twidere.twiderex.extensions.navViewModel
import com.twidere.twiderex.settings.types.SettingItem
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.settings.AppearanceViewModel

// light to dark
val primaryColors = listOf(
    Color(0XFF4C9EEB) to Color(0XFF5CB0FF),
    Color(0XFF1C68F3) to Color(0XFF4B85F0),
    Color(0XFF8D47EE) to Color(0XFF9254DE),
    Color(0XFFBC5077) to Color(0XFFF4769B),
    Color(0XFFFA541C) to Color(0XFFFF7A45),
    Color(0XFFFAAD14) to Color(0XFFFFC53D),
    Color(0XFF9ACB1E) to Color(0XFFBBE739),
    Color(0XFF38D29B) to Color(0XFF44ECAE),
)

class PrimaryColorSetting(
    private val preferences: SharedPreferences,
) : SettingItem<Int>() {
    @OptIn(ExperimentalUnsignedTypes::class)
    override fun save(value: Int) {
        preferences.edit {
            putInt(key, value)
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun load(): Int {
        return preferences.getInt(key, 0)
    }
}

val AmbientPrimaryColor = ambientOf<Int>()

@Composable
fun currentPrimaryColor(): Color {
    val colorIndex = AmbientPrimaryColor.current
    return if (isDarkTheme()) {
        primaryColors[colorIndex].second
    } else {
        primaryColors[colorIndex].first
    }
}

@Composable
fun primaryColorDialog(onDismiss: () -> Unit) {
    val colorIndex = AmbientPrimaryColor.current
    val viewModel = navViewModel<AppearanceViewModel>()
    val colors = if (isDarkTheme()) {
        primaryColors.map { it.second }
    } else {
        primaryColors.map { it.first }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Select color")
        },
        text = {
            LazyRowForIndexed(
                items = colors
            ) { index, it ->
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
                                    viewModel.primaryColorSettings.apply(index)
                                }
                            ),
                        alignment = Alignment.Center,
                    ) {
                        if (colorIndex == index) {
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
