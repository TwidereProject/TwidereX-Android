package com.twidere.twiderex.scenes

import androidx.compose.runtime.Composable
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.MediaType

@Composable
expect fun PlatformStatusMediaScene(statusKey: MicroBlogKey, selectedIndex: Int)

@Composable
expect fun PlatformRawMediaScene(url: String, type: MediaType)

@Composable
expect fun PlatformPureMediaScene(belongToKey: MicroBlogKey, selectedIndex: Int)