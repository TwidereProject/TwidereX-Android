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
package com.twidere.twiderex.component.foundation

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import dev.chrisbanes.accompanist.glide.GlideImage
import dev.chrisbanes.accompanist.imageloading.DataSource
import dev.chrisbanes.accompanist.imageloading.ImageLoadState

@Composable
fun NetworkImage(
    data: Any,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable (BoxScope.() -> Unit)? = null,
) {
    if (data is Painter) {
        Image(
            painter = data,
            modifier = modifier,
            contentScale = contentScale,
            contentDescription = stringResource(id = R.string.accessibility_common_network_image)
        )
    } else {

        GlideImage(
            data = data,
            modifier = modifier,
        ) { imageState ->
            when (imageState) {
                is ImageLoadState.Success -> {
                    MaterialLoadingImage(
                        result = imageState,
                        contentDescription = stringResource(id = R.string.accessibility_common_network_image),
                        contentScale = contentScale,
                    )
                }
                is ImageLoadState.Error -> Unit
                ImageLoadState.Loading -> if (placeholder != null) placeholder()
                ImageLoadState.Empty -> if (placeholder != null) placeholder()
            }
        }
    }
}

@Composable
fun MaterialLoadingImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
    fadeInEnabled: Boolean = true,
) {
    val cf = if (fadeInEnabled) {
        val fadeInTransition = updateFadeInTransition(key = painter)
        remember { ColorMatrix() }
            .apply {
                setAlpha(fadeInTransition.alpha)
            }
            .let { matrix ->
                ColorFilter.colorMatrix(matrix)
            }
    } else {
        colorFilter
    }

    Image(
        painter = painter,
        contentDescription = contentDescription,
        alignment = alignment,
        contentScale = contentScale,
        colorFilter = cf,
        modifier = modifier,
    )
}

@Composable
fun MaterialLoadingImage(
    result: ImageLoadState.Success,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
    skipFadeWhenLoadedFromMemory: Boolean = true,
    fadeInEnabled: Boolean = true,
) {
    MaterialLoadingImage(
        painter = result.painter,
        contentDescription = contentDescription,
        alignment = alignment,
        contentScale = contentScale,
        colorFilter = colorFilter,
        modifier = modifier,
        fadeInEnabled = fadeInEnabled && !(skipFadeWhenLoadedFromMemory && result.isFromMemory()),
    )
}

@Composable
private fun updateFadeInTransition(key: Any): FadeInTransition {
    val transitionState = remember(key) {
        MutableTransitionState(ImageLoadTransitionState.Empty).apply {
            targetState = ImageLoadTransitionState.Loaded
        }
    }

    val transition = updateTransition(transitionState)
    val alpha = transition.animateFloat(
        targetValueByState = { if (it == ImageLoadTransitionState.Loaded) 1f else 0f }
    )
    return remember(transition) { FadeInTransition(alpha) }
}

@Stable
private class FadeInTransition(
    alpha: State<Float> = mutableStateOf(0f),
) {
    val alpha by alpha
}

private enum class ImageLoadTransitionState { Loaded, Empty }

private fun ImageLoadState.Success.isFromMemory(): Boolean = source == DataSource.MEMORY

private fun ColorMatrix.setAlpha(alpha: Float) = set(row = 3, column = 3, v = alpha)
