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
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.settings.AmbientFontScale
import com.twidere.twiderex.settings.AmbientTheme
import com.twidere.twiderex.settings.AmbientUseSystemFontSize
import com.twidere.twiderex.settings.Theme
import com.twidere.twiderex.settings.currentPrimaryColor

@Composable
fun TwidereXTheme(
    requireDarkTheme: Boolean = false,
    pureStatusBarColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val theme = AmbientTheme.current
    val primaryColor = currentPrimaryColor()
    val useSystemFontSize = AmbientUseSystemFontSize.current
    val fontScale = AmbientFontScale.current

    val darkTheme = if (requireDarkTheme) {
        true
    } else {
        when (theme) {
            Theme.Auto -> isSystemInDarkTheme()
            Theme.Light -> false
            Theme.Dark -> true
        }
    }
    val colors = if (darkTheme) {
        darkColors(
            primary = primaryColor,
            primaryVariant = primaryColor,
            secondary = primaryColor,

            )
    } else {
        lightColors(
            primary = primaryColor,
            primaryVariant = primaryColor,
            secondary = primaryColor
        )
    }

    val typography = if (useSystemFontSize) {
        Typography()
    } else {
        Typography(
            h1 = TextStyle(
                fontWeight = FontWeight.Light,
                fontSize = 96.sp * fontScale,
                letterSpacing = (-1.5).sp
            ),
            h2 = TextStyle(
                fontWeight = FontWeight.Light,
                fontSize = 60.sp * fontScale,
                letterSpacing = (-0.5).sp
            ),
            h3 = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 48.sp * fontScale,
                letterSpacing = 0.sp
            ),
            h4 = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 34.sp * fontScale,
                letterSpacing = 0.25.sp
            ),
            h5 = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp * fontScale,
                letterSpacing = 0.sp
            ),
            h6 = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp * fontScale,
                letterSpacing = 0.15.sp
            ),
            subtitle1 = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp * fontScale,
                letterSpacing = 0.15.sp
            ),
            subtitle2 = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp * fontScale,
                letterSpacing = 0.1.sp
            ),
            body1 = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp * fontScale,
                letterSpacing = 0.5.sp
            ),
            body2 = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp * fontScale,
                letterSpacing = 0.25.sp
            ),
            button = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp * fontScale,
                letterSpacing = 1.25.sp
            ),
            caption = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp * fontScale,
                letterSpacing = 0.4.sp
            ),
            overline = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp * fontScale,
                letterSpacing = 1.5.sp
            )
        )
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = {
            val window = AmbientWindow.current
            val statusBarColor = if (pureStatusBarColor) {
                MaterialTheme.colors.surface
            } else {
                MaterialTheme.colors.surface.withElevation()
            }
            updateStatusBar(window, darkTheme, statusBarColor)
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
