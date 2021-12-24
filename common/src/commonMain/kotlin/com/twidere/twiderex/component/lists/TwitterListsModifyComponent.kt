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
package com.twidere.twiderex.component.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.ColoredSwitch
import com.twidere.twiderex.component.stringResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TwitterListsModifyComponent(
    name: String,
    desc: String,
    isPrivate: Boolean,
    onNameChanged: (name: String) -> Unit,
    onDescChanged: (desc: String) -> Unit,
    onPrivateChanged: (private: Boolean) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(TwitterListsModifyComponentDefaults.PagePadding)
    ) {
        val nameInputFocusRequester = remember { FocusRequester() }
        val descInputFocusRequester = remember { FocusRequester() }
        val nameTextFieldVale = remember {
            mutableStateOf(TextFieldValue(name, TextRange(name.length)))
        }
        val descTextFieldVale = remember {
            mutableStateOf(TextFieldValue(desc, TextRange(desc.length)))
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
                text = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_modify_name),
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(TwitterListsModifyComponentDefaults.TextFieldTitle.ContentPadding)
            )
        }
        LaunchedEffect(true) {
            nameInputFocusRequester.requestFocus()
            keyboardController?.show()
        }
        ProvideTextStyle(value = MaterialTheme.typography.subtitle1) {
            TextField(
                value = nameTextFieldVale.value,
                onValueChange = {
                    if (it.text != nameTextFieldVale.value.text)
                        onNameChanged(it.text)
                    nameTextFieldVale.value = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nameInputFocusRequester),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { descInputFocusRequester.requestFocus() }
                )
            )
        }
        Spacer(modifier = Modifier.height(TwitterListsModifyComponentDefaults.VerticalPadding))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
                text = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_modify_description),
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(TwitterListsModifyComponentDefaults.TextFieldTitle.ContentPadding)
            )
        }
        ProvideTextStyle(value = MaterialTheme.typography.subtitle1) {
            TextField(
                value = descTextFieldVale.value,
                onValueChange = {
                    if (it.text != descTextFieldVale.value.text)
                        onDescChanged(it.text)
                    descTextFieldVale.value = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(descInputFocusRequester),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                )
            )
        }
        Spacer(modifier = Modifier.height(TwitterListsModifyComponentDefaults.VerticalPadding))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_modify_private), style = MaterialTheme.typography.body1)
            ColoredSwitch(
                checked = isPrivate,
                onCheckedChange = onPrivateChanged,
            )
        }
    }
}

private object TwitterListsModifyComponentDefaults {
    val PagePadding = 20.dp
    val VerticalPadding = 20.dp

    object TextFieldTitle {
        val ContentPadding = PaddingValues(start = 16.dp)
    }
}
