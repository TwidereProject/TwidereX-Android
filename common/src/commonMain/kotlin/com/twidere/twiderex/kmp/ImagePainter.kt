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
package com.twidere.twiderex.kmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import com.seiko.imageloader.ImageLoaderBuilder
import com.seiko.imageloader.rememberAsyncImagePainter
import com.seiko.imageloader.request.ImageRequestBuilder
import com.twidere.services.http.authorization.Authorization
import com.twidere.services.http.config.HttpConfig
import com.twidere.twiderex.component.foundation.NetworkImageState
import com.twidere.twiderex.component.image.ImageEffects

@Composable
internal fun rememberNetworkImagePainter(
  data: Any,
  authorization: Authorization,
  httpConfig: HttpConfig,
  effects: ImageEffects,
  onImageStateChanged: (NetworkImageState) -> Unit
): Painter {
  val request = remember(data) { ImageRequestBuilder().data(data).build() }
  return rememberAsyncImagePainter(request)
}

fun ImageLoaderBuilder.commonConfig(): ImageLoaderBuilder {
  return this
}