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
package com.twidere.twiderex.ui

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.preferences.AmbientAppearancePreferences
import com.twidere.twiderex.preferences.AmbientDisplayPreferences
import com.twidere.twiderex.preferences.proto.AppearancePreferences
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import dev.chrisbanes.accompanist.insets.HorizontalSide
import dev.chrisbanes.accompanist.insets.navigationBarsHeight
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.navigationBarsWidth
import dev.chrisbanes.accompanist.insets.statusBarsHeight
import dev.chrisbanes.accompanist.insets.toPaddingValues

@Composable
fun TwidereXTheme(
    requireDarkTheme: Boolean = false,
    withSystemBarPadding: Boolean = true,
    content: @Composable () -> Unit
) {
    val appearance = AmbientAppearancePreferences.current
    val display = AmbientDisplayPreferences.current
    val theme = appearance.theme
    val primaryColor = currentPrimaryColor()
    val useSystemFontSize = display.useSystemFontSize
    val fontScale = display.fontScale

    val darkTheme = if (requireDarkTheme) {
        true
    } else {
        when (theme) {
            AppearancePreferences.Theme.Auto -> isSystemInDarkTheme()
            AppearancePreferences.Theme.Light -> false
            AppearancePreferences.Theme.Dark -> true
            else -> false
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
            updateStatusBar(window, darkTheme)
            val navigationBarColor = Color.Black
            val statusBarColor = MaterialTheme.colors.surface.withElevation()
            Box {
                Box(
                    modifier = if (withSystemBarPadding) {
                        val ime = AmbientWindowInsets.current.ime
                        val navigation = AmbientWindowInsets.current.navigationBars
                        val status = AmbientWindowInsets.current.statusBars
                        val actual = ime.copy(
                            left = ime.left.coerceAtLeast(navigation.left),
                            right = ime.right.coerceAtLeast(navigation.right),
                            bottom = ime.bottom.coerceAtLeast(navigation.bottom),
                            top = ime.top.coerceAtLeast(navigation.top) + status.top,
                        )
                        Modifier.padding(actual.toPaddingValues())
                    } else {
                        Modifier
                    }.align(Alignment.Center)
                ) {
                    content()
                }
                Spacer(
                    modifier = if (withSystemBarPadding) {
                        Modifier
                            .statusBarsHeight()
                            .navigationBarsPadding(bottom = false)
                            .zIndex(999F)
                            .fillMaxWidth()
                            .background(statusBarColor)
                    } else {
                        Modifier
                    }.align(Alignment.TopCenter)
                )
                Spacer(
                    modifier = if (withSystemBarPadding) {
                        Modifier
                            .navigationBarsWidth(HorizontalSide.Left)
                            .zIndex(999F)
                            .fillMaxHeight()
                            .background(navigationBarColor)
                    } else {
                        Modifier
                    }.align(Alignment.CenterStart)
                )
                Spacer(
                    modifier = if (withSystemBarPadding) {
                        Modifier
                            .navigationBarsWidth(HorizontalSide.Right)
                            .fillMaxHeight()
                            .zIndex(999F)
                            .background(navigationBarColor)
                    } else {
                        Modifier
                    }.align(Alignment.CenterEnd)
                )
                Spacer(
                    modifier = if (withSystemBarPadding) {
                        Modifier
                            .navigationBarsHeight()
                            .zIndex(999F)
                            .fillMaxWidth()
                            .background(navigationBarColor)
                    } else {
                        Modifier
                    }.align(Alignment.BottomCenter)
                )
            }
        }
    )
}

fun updateStatusBar(
    window: Window,
    darkTheme: Boolean,
) {
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
                decor.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            } else {
                decor.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
