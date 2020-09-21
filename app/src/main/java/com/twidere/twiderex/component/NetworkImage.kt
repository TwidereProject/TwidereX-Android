package com.twidere.twiderex.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import dev.chrisbanes.accompanist.coil.CoilImage


@Composable
fun NetworkImage(
    url: Any,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable (() -> Unit)? = null,
) {
    CoilImage(
        data = url,
        modifier = modifier,
        contentScale = contentScale,
        loading = placeholder
    )
}

