/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.scenes.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.navigation.AmbientNavigator

class SearchItem : HomeNavigationItem() {
    @Composable
    override val name: String
        get() = stringResource(R.string.title_search)
    override val route: String
        get() = "search"

    @Composable
    override val icon: VectorAsset
        get() = vectorResource(id = R.drawable.ic_search)
    override val withAppBar: Boolean
        get() = false

    @OptIn(ExperimentalFoundationApi::class, ExperimentalFocus::class)
    @Composable
    override fun onCompose() {
        val navigator = AmbientNavigator.current
        Scaffold(
            topBar = {
                AppBar(
                    title = {
                        ProvideTextStyle(value = MaterialTheme.typography.body1) {
                            Row(
                                modifier = Modifier.clickable(
                                    onClick = {
                                        navigator.searchInput()
                                    },
                                    indication = null,
                                )
                            ) {
                                Providers(
                                    AmbientContentAlpha provides ContentAlpha.medium
                                ) {
                                    Text(
                                        modifier = Modifier.weight(1F)
                                            .align(Alignment.CenterVertically),
                                        text = stringResource(id = R.string.search_hint),
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        navigator.searchInput()
                                    }
                                ) {
                                    Icon(asset = vectorResource(id = R.drawable.ic_search))
                                }
                            }
                        }
                    }
                )
            }
        ) {
        }
    }
}
