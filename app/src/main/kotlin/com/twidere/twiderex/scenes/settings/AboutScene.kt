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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.twidere.twiderex.BuildConfig
import com.twidere.twiderex.R
import com.twidere.twiderex.component.LoginLogo
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.navigation.AmbientNavigator
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme

@Composable
fun AboutScene() {
    TwidereXTheme {
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
    val navigator = AmbientNavigator.current
    val navController = AmbientNavController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1.5F),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginLogo(modifier = Modifier.preferredWidth(150.dp))
            Box(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.h5,
            )
            Text(
                text = BuildConfig.VERSION_NAME,
            )
        }
        Box(modifier = Modifier.height(64.dp))
        Divider(
            modifier = Modifier.padding(horizontal = 64.dp)
        )
        Box(modifier = Modifier.height(64.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1F),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                IconButton(
                    onClick = {
                        navController.navigate(
                            Route.User(
                                "TwidereProject",
                                MicroBlogKey.TwitterHost,
                                MicroBlogKey.twitter("TwidereProject")
                            )
                        )
                    }
                ) {
                    Icon(
                        imageVector = vectorResource(id = R.drawable.ic_twitter),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = stringResource(id = R.string.accessibility_common_logo_twitter)
                    )
                }
                Box(modifier = Modifier.width(32.dp))
                IconButton(
                    onClick = {
                        navigator.openLink("https://github.com/TwidereProject/TwidereX-Android")
                    }
                ) {
                    Icon(
                        imageVector = vectorResource(id = R.drawable.ic_github),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = stringResource(id = R.string.accessibility_common_logo_github)
                    )
                }
            }
            Box(modifier = Modifier.height(32.dp))
            TextButton(
                onClick = {
                    navigator.openLink("https://github.com/TwidereProject/TwidereX-Android/blob/develop/LICENSE")
                }
            ) {
                Text(text = stringResource(id = R.string.scene_settings_about_license))
            }
        }
    }
}
