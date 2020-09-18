package com.twidere.twiderex.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Box
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.painter.ImagePainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ContextAmbient
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


@Composable
fun GlideImage(
    model: Any,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
) {
    WithConstraints(
        modifier
    ) {
        val width =
            if (constraints.maxWidth > 0 && constraints.maxWidth < Int.MAX_VALUE) {
                constraints.maxWidth
            } else {
                SIZE_ORIGINAL
            }

        val height =
            if (constraints.maxHeight > 0 && constraints.maxHeight < Int.MAX_VALUE) {
                constraints.maxHeight
            } else {
                SIZE_ORIGINAL
            }

        val image = fetchImage(model = model, width, height)
        Crossfade(current = image) {
            if (it != null) {
                Image(
                    painter = ImagePainter(it),
                    alignment = alignment,
                    contentScale = contentScale,
                )
            } else {
                Box()
            }
        }
    }
}


@Composable
fun fetchImage(
    model: Any,
    width: Int,
    height: Int,
): ImageAsset? {
    var image by remember(model) { mutableStateOf<ImageAsset?>(null) }
    val context = ContextAmbient.current
//    launchInComposition {
//        image = loadImage(model, context, width, height).asImageAsset()
//    }
    return image
}

private suspend fun loadImage(
    model: Any,
    context: Context,
    width: Int,
    height: Int,
) = suspendCancellableCoroutine<Bitmap> { c ->
    Glide.with(context)
        .asBitmap()
        .load(model)
        .override(width, height)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                c.resume(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                c.cancel()
            }
        })

}
