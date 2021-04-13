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
package com.twidere.twiderex.scenes.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.BuildConfig
import com.twidere.twiderex.R
import com.twidere.twiderex.component.LoginLogo
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.ui.standardPadding

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
                        Text(text = stringResource(id = R.string.scene_settings_about_title))
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(standardPadding * 3),
    ) {
        // Background and header
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_about_logo_shadow),
                contentDescription = stringResource(id = R.string.scene_settings_about_logo_background_shadow),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = standardPadding * 4)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_about_logo),
                contentDescription = stringResource(id = R.string.scene_settings_about_logo_background),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = standardPadding)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LoginLogo(modifier = Modifier.width(44.dp))
                Box(modifier = Modifier.width(25.dp))
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.h4,
                )
            }
        }

        // version name
        Text(
            text = stringResource(id = R.string.scene_settings_about_version, BuildConfig.VERSION_NAME),
        )
        Box(modifier = Modifier.height(20.dp))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(id = R.string.scene_settings_about_description),
                style = MaterialTheme.typography.body2,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1F),
            verticalAlignment = Alignment.Bottom
        ) {
            Row {
                IconButton(
                    onClick = {
                        navController.navigate("deeplink/twitter/user/TwidereProject")
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_twitter),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = stringResource(id = R.string.accessibility_common_logo_twitter)
                    )
                }
                Box(modifier = Modifier.width(standardPadding * 3))
                IconButton(
                    onClick = {
                        navigator.openLink("https://github.com/TwidereProject/TwidereX-Android")
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_github),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = stringResource(id = R.string.accessibility_common_logo_github)
                    )
                }
                Box(modifier = Modifier.width(standardPadding * 3))
                IconButton(
                    onClick = {
                        navigator.openLink("https://t.me/twidere_x")
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_telegram),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = stringResource(id = R.string.accessibility_common_logo_github)
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
                        text = stringResource(id = R.string.scene_settings_about_license),
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .padding(standardPadding)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    Box(modifier = Modifier.height(3.dp))
                    Divider(
                        thickness = 2.dp,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colors.primary
                    )
                }
            }
        }
    }
}
