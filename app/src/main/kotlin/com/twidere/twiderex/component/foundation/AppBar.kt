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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.twidere.twiderex.ui.LocalIsActiveEdgeToEdge

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation
) {
    StatusTopAppBar(
        title,
        if (LocalIsActiveEdgeToEdge.current) modifier.statusBarsPadding()
            .navigationBarsPadding(bottom = false)
        else modifier,
        navigationIcon, actions, backgroundColor, contentColor, elevation,
    )
}

@Composable
fun StatusTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = androidx.compose.material.AppBarDefaults.TopAppBarElevation
) {
    PaddingSurface(
        color = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        shape = RectangleShape,
        modifier = modifier
    ) {
        Row(
            Modifier.fillMaxWidth()
                .padding(AppBarDefaults.ContentPadding)
                .height(AppBarDefaults.AppBarHeight),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (navigationIcon == null) {
                Spacer(AppBarDefaults.TitleInsetWithoutIcon)
            } else {
                Row(AppBarDefaults.TitleIconModifier, verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        content = navigationIcon
                    )
                }
            }

            Row(
                Modifier.fillMaxHeight().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProvideTextStyle(value = MaterialTheme.typography.h6) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        content = title
                    )
                }
            }

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Row(
                    Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )
            }
        }
    }
}

object AppBarDefaults {
    val TopAppBarElevation = 4.dp

    /**
     * Default elevation used for [BottomAppBar].
     */
    val BottomAppBarElevation = 8.dp

    private val AppBarHorizontalPadding = 4.dp

    /**
     * Default padding used for [TopAppBar] and [BottomAppBar].
     */
    val ContentPadding = PaddingValues(
        start = AppBarHorizontalPadding,
        end = AppBarHorizontalPadding
    )

    val AppBarHeight = 56.dp

    val TitleInsetWithoutIcon = Modifier.width(16.dp - AppBarHorizontalPadding)
    // Start inset for the title when there is a navigation icon provided
    val TitleIconModifier = Modifier.fillMaxHeight()
        .width(72.dp - AppBarHorizontalPadding)
}
