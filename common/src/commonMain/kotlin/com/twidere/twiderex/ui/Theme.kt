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
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
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
import com.twidere.twiderex.preferences.model.AppearancePreferences

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
            content = content,
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
    val currentDarkTheme = isDarkTheme(requireDarkTheme = false)
    val windowInsetsController = LocalWindowInsetsController.current
    DisposableEffect(currentDarkTheme) {
        onDispose {
            windowInsetsController.isAppearanceLightStatusBars = !currentDarkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !currentDarkTheme
        }
    }
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
        val windowInsetsController = LocalWindowInsetsController.current
        LaunchedEffect(darkTheme) {
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
        }
        val statusBarColor = statusBarColorProvider.invoke()
        val navigationBarColor = navigationBarColorProvider.invoke().let {
            val surface = MaterialTheme.colors.surface
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && !darkTheme && it == surface) {
                MaterialTheme.colors.onSurface
            } else {
                it
            }
        }
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
private fun isDarkTheme(requireDarkTheme: Boolean = false): Boolean {
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
