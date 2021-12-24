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
package com.twidere.twiderex.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.twidere.twiderex.component.NativeInsetsColor
import com.twidere.twiderex.component.NativeInsetsControl
import com.twidere.twiderex.component.PlatformInsets
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.kmp.ProvideOverScrollConfiguration
import com.twidere.twiderex.kmp.isAndroidLessOreo
import com.twidere.twiderex.preferences.LocalAppearancePreferences
import com.twidere.twiderex.preferences.LocalDisplayPreferences
import com.twidere.twiderex.preferences.model.AppearancePreferences

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TwidereTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = provideThemeColors(darkTheme)
    val pureDark = LocalAppearancePreferences.current.isDarkModePureBlack
    val typography = provideTypography()
    CompositionLocalProvider(
        *if (pureDark && darkTheme) {
            arrayOf(LocalElevationOverlay provides null)
        } else {
            emptyArray()
        }
    ) {
        MaterialTheme(
            colors = colors,
            typography = typography,
            shapes = shapes,
            content = {
                ProvideOverScrollConfiguration {
                    content.invoke()
                }
            },
        )
    }
}

@Composable
fun TwidereDialog(
    requireDarkTheme: Boolean = false,
    extendViewIntoStatusBar: Boolean = false,
    extendViewIntoNavigationBar: Boolean = false,
    statusBarColorProvider: @Composable () -> Color = {
        MaterialTheme.colors.surface.withElevation()
    },
    navigationBarColorProvider: @Composable () -> Color = {
        MaterialTheme.colors.surface
    },
    content: @Composable () -> Unit,
) {
    TwidereScene(
        requireDarkTheme,
        extendViewIntoStatusBar,
        extendViewIntoNavigationBar,
        statusBarColorProvider,
        navigationBarColorProvider,
        content,
    )
}

@Composable
fun TwidereScene(
    requireDarkTheme: Boolean = false,
    extendViewIntoStatusBar: Boolean = false,
    extendViewIntoNavigationBar: Boolean = false,
    statusBarColorProvider: @Composable () -> Color = {
        MaterialTheme.colors.surface.withElevation()
    },
    navigationBarColorProvider: @Composable () -> Color = {
        MaterialTheme.colors.surface
    },
    content: @Composable () -> Unit,
) {
    val darkTheme = isDarkTheme(requireDarkTheme)
    TwidereTheme(darkTheme = darkTheme) {
        val statusBarColor = statusBarColorProvider.invoke()
        val navigationBarColor = navigationBarColorProvider.invoke().let {
            val surface = MaterialTheme.colors.surface
            if (isAndroidLessOreo && !darkTheme && it == surface) {
                MaterialTheme.colors.onSurface
            } else {
                it
            }
        }
        PlatformInsets(
            control = NativeInsetsControl(
                extendToTop = extendViewIntoStatusBar,
                extendToBottom = extendViewIntoNavigationBar,
                extendToStart = extendViewIntoNavigationBar,
                extendToEnd = extendViewIntoNavigationBar,
            ),
            color = NativeInsetsColor(
                top = statusBarColor,
                start = navigationBarColor,
                end = navigationBarColor,
                bottom = navigationBarColor,
            ),
            content = content,
        )
    }
}

@Composable
fun isDarkTheme(requireDarkTheme: Boolean = false): Boolean {
    val appearance = LocalAppearancePreferences.current
    val theme = appearance.theme
    return if (requireDarkTheme) {
        true
    } else {
        when (theme) {
            AppearancePreferences.Theme.Auto -> isSystemInDarkTheme()
            AppearancePreferences.Theme.Light -> false
            AppearancePreferences.Theme.Dark -> true
        }
    }
}

@Composable
private fun provideTypography(): Typography {
    val display = LocalDisplayPreferences.current
    val useSystemFontSize = display.useSystemFontSize
    val fontScale = display.fontScale
    val baseTypography = Typography()
        // classical text rendering (like the old twidere)
        .let {
            it.copy(
                body1 = it.body1.copy(fontSize = 14.sp, letterSpacing = 0.sp),
                body2 = it.body2.copy(letterSpacing = 0.sp),
                caption = it.caption.copy(letterSpacing = 0.sp),
            )
        }
    return if (useSystemFontSize) {
        baseTypography
    } else {
        baseTypography.let {
            it.copy(
                h1 = it.h1.copy(
                    fontSize = it.h1.fontSize * fontScale,
                ),
                h2 = it.h2.copy(
                    fontSize = it.h2.fontSize * fontScale,
                ),
                h3 = it.h3.copy(
                    fontSize = it.h3.fontSize * fontScale,
                ),
                h4 = it.h4.copy(
                    fontSize = it.h4.fontSize * fontScale,
                ),
                h5 = it.h5.copy(
                    fontSize = it.h5.fontSize * fontScale,
                ),
                h6 = it.h6.copy(
                    fontSize = it.h6.fontSize * fontScale,
                ),
                subtitle1 = it.subtitle1.copy(
                    fontSize = it.subtitle1.fontSize * fontScale,
                ),
                subtitle2 = it.subtitle2.copy(
                    fontSize = it.subtitle2.fontSize * fontScale,
                ),
                body1 = it.body1.copy(
                    fontSize = it.body1.fontSize * fontScale,
                ),
                body2 = it.body2.copy(
                    fontSize = it.body2.fontSize * fontScale,
                ),
                button = it.button.copy(
                    fontSize = it.button.fontSize * fontScale,
                ),
                caption = it.caption.copy(
                    fontSize = it.caption.fontSize * fontScale,
                ),
                overline = it.overline.copy(
                    fontSize = it.overline.fontSize * fontScale,
                ),
            )
        }
    }
}

@Composable
private fun provideThemeColors(darkTheme: Boolean): Colors {
    val primaryColor by animateColorAsState(targetValue = currentPrimaryColor())
    val pureDark = LocalAppearancePreferences.current.isDarkModePureBlack
    val target = if (darkTheme) {
        if (pureDark) {
            darkColors(
                primary = primaryColor,
                primaryVariant = primaryColor,
                secondary = primaryColor,
                background = Color.Black,
                surface = Color.Black,
            )
        } else {
            darkColors(
                primary = primaryColor,
                primaryVariant = primaryColor,
                secondary = primaryColor,
            )
        }
    } else {
        lightColors(
            primary = primaryColor,
            primaryVariant = primaryColor,
            secondary = primaryColor
        )
    }
    val background by animateColorAsState(targetValue = target.background)
    val surface by animateColorAsState(targetValue = target.surface)
    val error by animateColorAsState(targetValue = target.error)
    val onPrimary by animateColorAsState(targetValue = target.onPrimary)
    val onSecondary by animateColorAsState(targetValue = target.onSecondary)
    val onBackground by animateColorAsState(targetValue = target.onBackground)
    val onSurface by animateColorAsState(targetValue = target.onSurface)
    val onError by animateColorAsState(targetValue = target.onError)
    return target.copy(
        primary = primaryColor,
        primaryVariant = primaryColor,
        secondary = primaryColor,
        background = background,
        surface = surface,
        error = error,
        onPrimary = onPrimary,
        onSecondary = onSecondary,
        onBackground = onBackground,
        onSurface = onSurface,
        onError = onError,
    )
}
