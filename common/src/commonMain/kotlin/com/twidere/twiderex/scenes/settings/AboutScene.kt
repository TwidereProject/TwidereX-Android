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
package com.twidere.twiderex.scenes.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.BuildConfig
import com.twidere.twiderex.component.LoginLogo
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.ParallaxLayout
import com.twidere.twiderex.component.foundation.rememberParallaxLayoutState
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.navigation.RootDeepLinks
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene

@Composable
fun AboutScene() {
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_about_title))
                    }
                )
            }
        ) {
            AboutContent()
        }
    }
}

@Composable
private fun AboutContent() {
    val navigator = LocalNavigator.current
    val navController = LocalNavController.current
    val parallaxLayoutState = rememberParallaxLayoutState(maxRotate = 2f, maxTransition = 50f)
    Column(
        modifier = Modifier
            .padding(AboutContentDefaults.ContentPadding)
            .fillMaxWidth()
    ) {
        // Background and header
        Box(
            modifier = Modifier
                .weight(4F)
        ) {
            val aspectRatio = 1.4f // from ui design

            ParallaxLayout(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.TopEnd)
                    .horizontalScroll(state = ScrollState(0), enabled = false),
                alignment = Alignment.TopEnd,
                backContentOffsetX = AboutContentDefaults.BackContentOffsetX,
                backContentOffsetY = AboutContentDefaults.BackContentOffsetY,
                parallaxLayoutState = parallaxLayoutState,
                backContent = {
                    Image(
                        painter = painterResource(com.twidere.twiderex.MR.files.ic_about_gray_logo_shadow),
                        contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_about_logo_background_shadow),
                        modifier = Modifier
                            .blur(30.dp)
                            .aspectRatio(aspectRatio)
                            .fillMaxHeight()
                            .padding(
                                start = AboutContentDefaults.ParallaxPaddingStart,
                                top = AboutContentDefaults.ParallaxPaddingTop,
                                bottom = AboutContentDefaults.ParallaxPaddingBottom
                            ),
                    )
                }
            ) {
                Image(
                    painter = painterResource(res = com.twidere.twiderex.MR.files.ic_about_gray_logo),
                    contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_about_logo_background),
                    modifier = Modifier
                        .aspectRatio(aspectRatio)
                        .fillMaxHeight()
                        .padding(
                            start = AboutContentDefaults.ParallaxPaddingStart,
                            top = AboutContentDefaults.ParallaxPaddingTop,
                            bottom = AboutContentDefaults.ParallaxPaddingBottom
                        )
                        .alpha(0.5f)
                )
            }

            Row(
                modifier = Modifier
                    .padding(AboutContentDefaults.Logo.ContentPadding)
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LoginLogo(modifier = Modifier.width(AboutContentDefaults.Logo.Size))
                Box(modifier = Modifier.width(AboutContentDefaults.Logo.IconSpacing))
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = "Twidere X",
                        style = MaterialTheme.typography.h4,
                    )
                }
            }

            // version name
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(AboutContentDefaults.VersionName.ContentPadding)
            ) {
                Text(
                    text = stringResource(
                        res = com.twidere.twiderex.MR.strings.scene_settings_about_version,
                        BuildConfig.VERSION_NAME
                    ),
                )
                Box(modifier = Modifier.height(AboutContentDefaults.VersionName.Spacing))
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_about_description),
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1F))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AboutContentDefaults.Icon.ContentPadding),
            verticalAlignment = Alignment.Bottom
        ) {
            Row {
                IconButton(
                    onClick = {
                        navController.navigate(RootDeepLinks.Twitter.User("TwidereProject"))
                    }
                ) {
                    Icon(
                        painter = painterResource(res = com.twidere.twiderex.MR.files.ic_twitter),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_common_logo_twitter)
                    )
                }
                Box(modifier = Modifier.width(AboutContentDefaults.Icon.Spacing))
                IconButton(
                    onClick = {
                        navigator.openLink("https://github.com/TwidereProject/TwidereX-Android")
                    }
                ) {
                    Icon(
                        painter = painterResource(res = com.twidere.twiderex.MR.files.ic_github),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_common_logo_github)
                    )
                }
                Box(modifier = Modifier.width(AboutContentDefaults.Icon.Spacing))
                IconButton(
                    onClick = {
                        navigator.openLink("https://t.me/twidere_x")
                    }
                ) {
                    Icon(
                        painter = painterResource(res = com.twidere.twiderex.MR.files.ic_telegram),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = stringResource(res = com.twidere.twiderex.MR.strings.accessibility_common_logo_github)
                    )
                }
            }
            Box(modifier = Modifier.weight(1F))
            TextButton(
                onClick = {
                    navigator.openLink("https://github.com/TwidereProject/TwidereX-Android/blob/develop/LICENSE")
                },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.typography.body1.color)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                ) {
                    Text(
                        text = stringResource(res = com.twidere.twiderex.MR.strings.scene_settings_about_license),
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(AboutContentDefaults.License.TextPadding)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    Box(modifier = Modifier.height(AboutContentDefaults.License.DividerSpacing))
                    Divider(
                        thickness = AboutContentDefaults.License.DividerThickness,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colors.primary
                    )
                }
            }
        }
    }
}

private object AboutContentDefaults {
    val ContentPadding = PaddingValues(
        horizontal = 0.dp,
        vertical = 24.dp,
    )
    val ParallaxPaddingStart = 32.dp
    val ParallaxPaddingTop = 40.dp
    val ParallaxPaddingBottom = 80.dp
    val BackContentOffsetX = (-24).dp
    val BackContentOffsetY = 24.dp
    object Logo {
        val Size = 44.dp
        val IconSpacing = 24.dp
        val ContentPadding = PaddingValues(
            horizontal = 24.dp,
            vertical = 0.dp
        )
    }
    object VersionName {
        val ContentPadding = PaddingValues(
            horizontal = 24.dp,
            vertical = 0.dp
        )
        val Spacing = 24.dp
    }
    object Icon {
        val ContentPadding = PaddingValues(
            horizontal = 24.dp,
            vertical = 0.dp
        )
        val Spacing = 24.dp
    }
    object License {
        val DividerSpacing = 4.dp
        val TextPadding = PaddingValues(8.dp)
        val DividerThickness = 2.dp
    }
}
