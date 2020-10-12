package com.twidere.twiderex.ui

import androidx.compose.foundation.contentColor
import androidx.compose.material.EmphasisAmbient
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val blue = Color(0xff4C9EEB)


@Composable
val mediumEmphasisContentContentColor: Color
    get() = EmphasisAmbient.current.medium.applyEmphasis(
        contentColor()
    )