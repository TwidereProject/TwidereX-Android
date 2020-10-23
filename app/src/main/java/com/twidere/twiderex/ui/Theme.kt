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
package com.twidere.twiderex.ui

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.ui.graphics.toArgb
import com.twidere.twiderex.extensions.WindowAmbient
import com.twidere.twiderex.extensions.withElevation

private val DarkColorPalette = darkColors(
    primary = blue,
    primaryVariant = blue,
    secondary = blue
)

private val LightColorPalette = lightColors(
    primary = blue,
    primaryVariant = blue,
    secondary = blue
)

@Composable
fun TwidereXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureStatusBarColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = {
            val window = WindowAmbient.current
            val statusBarColor = if (pureStatusBarColor) {
                MaterialTheme.colors.surface
            } else {
                MaterialTheme.colors.surface.withElevation()
            }
            onActive {
                updateStatusBar(window, darkTheme, statusBarColor)
            }
            content()
        }
    )
}

fun updateStatusBar(
    window: Window,
    darkTheme: Boolean,
    statusBarColor: androidx.compose.ui.graphics.Color,
) {
    window.statusBarColor = statusBarColor.toArgb()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                if (darkTheme) 0 else WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            val decor = window.decorView
            @Suppress("DEPRECATION")
            decor.systemUiVisibility = if (darkTheme) {
                0
            } else {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
