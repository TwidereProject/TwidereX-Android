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
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.insets.HorizontalSide
import com.google.accompanist.insets.Insets
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.navigationBarsWidth
import com.google.accompanist.insets.statusBarsHeight
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.preferences.LocalAppearancePreferences
import com.twidere.twiderex.preferences.LocalDisplayPreferences
import com.twidere.twiderex.preferences.proto.AppearancePreferences

@Composable
fun TwidereTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = provideThemeColors(darkTheme)
    val typography = provideTypography()
    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content,
    )
}

@Composable
fun TwidereDialog(
    requireDarkTheme: Boolean = false,
    extendViewIntoStatusBar: Boolean = false,
    extendViewIntoNavigationBar: Boolean = false,
    content: @Composable () -> Unit,
) {
    val currentDarkTheme = isDarkTheme(requireDarkTheme = false)
    val windowInsetsController = LocalWindowInsetsController.current
    DisposableEffect(currentDarkTheme) {
        onDispose {
            windowInsetsController.isAppearanceLightStatusBars = !currentDarkTheme
        }
    }
    TwidereScene(
        requireDarkTheme,
        extendViewIntoStatusBar,
        extendViewIntoNavigationBar,
        content,
    )
}

@Composable
fun TwidereScene(
    requireDarkTheme: Boolean = false,
    extendViewIntoStatusBar: Boolean = false,
    extendViewIntoNavigationBar: Boolean = false,
    content: @Composable () -> Unit,
) {
    val darkTheme = isDarkTheme(requireDarkTheme)
    TwidereTheme(darkTheme = darkTheme) {
        val windowInsetsController = LocalWindowInsetsController.current
        LaunchedEffect(darkTheme) {
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
        }
        val navigationBarColor = navigationBarColor(darkTheme)
        val statusBarColor = statusBarColor()
        Box {
            val actual = provideSystemInsets(
                extendViewIntoNavigationBar,
                extendViewIntoStatusBar
            )
            Box(
                modifier = Modifier
                    .padding(
                        actual.let {
                            with(LocalDensity.current) {
                                val layoutDirection = LocalLayoutDirection.current
                                PaddingValues(
                                    top = it.top.toDp(),
                                    bottom = it.bottom.toDp(),
                                    start = when (layoutDirection) {
                                        LayoutDirection.Ltr -> it.left.toDp()
                                        LayoutDirection.Rtl -> it.right.toDp()
                                    },
                                    end = when (layoutDirection) {
                                        LayoutDirection.Ltr -> it.right.toDp()
                                        LayoutDirection.Rtl -> it.left.toDp()
                                    },

                                )
                            }
                        }
                    )
                    .align(Alignment.Center)
            ) {
                content()
            }
            Spacer(
                modifier = if (!extendViewIntoStatusBar) {
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
                modifier = if (!extendViewIntoNavigationBar) {
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
                modifier = if (!extendViewIntoNavigationBar) {
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
                modifier = if (!extendViewIntoNavigationBar) {
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
}

@Composable
fun navigationBarColor(darkTheme: Boolean): Color {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
        Color.Black
    } else {
        if (darkTheme) {
            Color.Black
        } else {
            Color(0xFFF1F1F1)
        }
    }
}

@Composable
fun statusBarColor(): Color {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        Color.Black
    } else {
        MaterialTheme.colors.surface.withElevation()
    }
}

@Composable
private fun provideSystemInsets(
    extendViewIntoNavigationBar: Boolean,
    extendViewIntoStatusBar: Boolean
): Insets {
    val ime = LocalWindowInsets.current.ime
    val navigation = LocalWindowInsets.current.navigationBars
    val status = LocalWindowInsets.current.statusBars
    return key(
        ime,
        ime.isVisible,
        navigation,
        status
    ) {
        ime.copy(
            left = if (extendViewIntoNavigationBar) {
                0
            } else {
                ime.left.coerceAtLeast(navigation.left)
            },
            right = if (extendViewIntoNavigationBar) {
                0
            } else {
                ime.right.coerceAtLeast(navigation.right)
            },
            bottom = if (extendViewIntoNavigationBar) {
                0
            } else {
                ime.bottom.coerceAtLeast(navigation.bottom)
            },
            top = if (extendViewIntoNavigationBar) {
                0
            } else {
                ime.top.coerceAtLeast(navigation.top)
            } + if (extendViewIntoStatusBar) {
                0
            } else {
                status.top
            },
        )
    }
}

@Composable
private fun isDarkTheme(requireDarkTheme: Boolean): Boolean {
    val appearance = LocalAppearancePreferences.current
    val theme = appearance.theme
    return if (requireDarkTheme) {
        true
    } else {
        when (theme) {
            AppearancePreferences.Theme.Auto -> isSystemInDarkTheme()
            AppearancePreferences.Theme.Light -> false
            AppearancePreferences.Theme.Dark -> true
            else -> false
        }
    }
}

@Composable
private fun provideTypography(): Typography {
    val display = LocalDisplayPreferences.current
    val useSystemFontSize = display.useSystemFontSize
    val fontScale = display.fontScale
    return if (useSystemFontSize) {
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
}

@Composable
private fun provideThemeColors(darkTheme: Boolean): Colors {
    val primaryColor by animateColorAsState(targetValue = currentPrimaryColor())
    val target = if (darkTheme) {
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
