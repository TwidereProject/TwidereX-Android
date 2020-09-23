package com.twidere.twiderex.ui

import androidx.compose.foundation.contentColor
import androidx.compose.material.EmphasisAmbient
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val purple200 = Color(0xFFBB86FC)
val purple500 = Color(0xFF6200EE)
val purple700 = Color(0xFF3700B3)
val teal200 = Color(0xFF03DAC5)


@Composable
val buttonContentColor: Color
    get() = EmphasisAmbient.current.medium.applyEmphasis(
        contentColor()
    )