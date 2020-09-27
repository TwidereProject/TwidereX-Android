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
            val statusBarColor = MaterialTheme.colors.surface
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
            decor.systemUiVisibility = if (darkTheme) {
                0
            } else {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}