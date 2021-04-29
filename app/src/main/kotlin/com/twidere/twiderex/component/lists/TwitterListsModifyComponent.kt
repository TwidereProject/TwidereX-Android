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
package com.twidere.twiderex.component.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.TextInput

@Composable
fun TwitterListsModifyComponent(
    name: String,
    desc: String,
    isPrivate: Boolean,
    onNameChanged: (name: String) -> Unit,
    onDescChanged: (desc: String) -> Unit,
    onPrivateChanged: (private: Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(TwitterListsModifyComponentDefaults.PagePadding)
    ) {
        val focusRequester = remember { FocusRequester() }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
                text = stringResource(id = R.string.scene_lists_modify_name),
                style = MaterialTheme.typography.caption
            )
        }
        Spacer(modifier = Modifier.height(TwitterListsModifyComponentDefaults.VerticalPadding))
        TextInput(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = name,
            onValueChange = onNameChanged,
            autoFocus = true,
            placeholder = {
                Text(text = stringResource(id = R.string.scene_lists_modify_name))
            }
        )
        Spacer(modifier = Modifier.height(TwitterListsModifyComponentDefaults.VerticalPadding))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
                text = stringResource(id = R.string.scene_lists_modify_description),
                style = MaterialTheme.typography.caption
            )
        }
        ProvideTextStyle(value = MaterialTheme.typography.subtitle1) {
            TextInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = desc,
                onValueChange = onDescChanged,
                autoFocus = false,
                imeAction = ImeAction.Done,
            )
        }
        Spacer(modifier = Modifier.height(TwitterListsModifyComponentDefaults.VerticalPadding))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = stringResource(id = R.string.scene_lists_modify_private))
            Switch(checked = isPrivate, onCheckedChange = onPrivateChanged)
        }
    }
}

object TwitterListsModifyComponentDefaults {
    val PagePadding = 20.dp
    val VerticalPadding = 20.dp
}
