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
package com.twidere.twiderex.scenes.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.TextInput
import com.twidere.twiderex.component.navigation.AmbientNavigator
import com.twidere.twiderex.ui.TwidereXTheme

@OptIn(ExperimentalFoundationApi::class, ExperimentalFocus::class)
@Composable
fun SearchInputScene(initial: String? = null) {
    val initialText = initial ?: ""
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialText,
                selection = TextRange(initialText.length),
            )
        )
    }
    val navigator = AmbientNavigator.current
    TwidereXTheme {
        Scaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        ProvideTextStyle(value = MaterialTheme.typography.body1) {
                            TextInput(
                                value = textFieldValue,
                                onValueChange = {
                                    textFieldValue = it
                                },
                                maxLines = 1,
                                placeholder = {
                                    Text(text = stringResource(id = R.string.search_hint))
                                },
                                onImeActionPerformed = { _, _ ->
                                    if (textFieldValue.text.isNotEmpty()) {
                                        navigator.search(textFieldValue.text)
                                    }
                                },
                                autoFocus = true,
                                imeAction = ImeAction.Search,
                                alignment = Alignment.CenterStart,
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (textFieldValue.text.isNotEmpty()) {
                                    navigator.search(textFieldValue.text)
                                }
                            }
                        ) {
                            Icon(asset = vectorResource(id = R.drawable.ic_search))
                        }
                    }
                )
            }
        ) {
        }
    }
}
