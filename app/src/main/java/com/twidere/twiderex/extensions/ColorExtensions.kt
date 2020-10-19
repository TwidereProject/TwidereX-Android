package com.twidere.twiderex.extensions

import androidx.compose.material.AmbientElevationOverlay
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.twidere.twiderex.component.TopAppBarElevation

@Composable
fun Color.withElevation(elevation: Dp = TopAppBarElevation): Color {
    return AmbientElevationOverlay.current?.apply(
        color = this,
        elevation = elevation
    ) ?: this
}