/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.component.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.Icon
import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.text.input.ImeAction
import com.twidere.twiderex.R
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.TextInput
import com.twidere.twiderex.extensions.NavControllerAmbient
import com.twidere.twiderex.fragment.SearchFragmentArgs

class SearchItem : HomeNavigationItem() {
    override val name: String
        get() = "Search"
    override val icon: VectorAsset
        get() = Icons.Default.Search
    override val withAppBar: Boolean
        get() = false

    @OptIn(ExperimentalFoundationApi::class, ExperimentalFocus::class)
    @Composable
    override fun onCompose() {
        val (text, setText) = remember { mutableStateOf("") }
        val navController = NavControllerAmbient.current
        Scaffold(
            topBar = {
                AppBar(
                    title = {
                        ProvideTextStyle(value = MaterialTheme.typography.body1) {
                            Row {
                                TextInput(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .weight(1F),
                                    value = text,
                                    onValueChange = {
                                        setText(it)
                                    },
                                    placeholder = {
                                        Text(text = "Tap to search...")
                                    },
                                    imeAction = ImeAction.Search,
                                    alignment = Alignment.CenterStart,
                                    onImeActionPerformed = { _, _ ->
                                        if (text.isNotEmpty()) {
                                            navController.navigate(
                                                R.id.search_fragment,
                                                SearchFragmentArgs(text).toBundle(),
                                            )
                                        }
                                    }
                                )
                                IconButton(
                                    onClick = {
                                        if (text.isNotEmpty()) {
                                            navController.navigate(
                                                R.id.search_fragment,
                                                SearchFragmentArgs(text).toBundle(),
                                            )
                                        }
                                    }
                                ) {
                                    Icon(asset = Icons.Default.Search)
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
