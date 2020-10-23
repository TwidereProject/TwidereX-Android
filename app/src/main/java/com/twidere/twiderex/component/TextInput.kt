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
package com.twidere.twiderex.component

import androidx.compose.foundation.AmbientTextStyle
import androidx.compose.foundation.BaseTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.ProvideEmphasis
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.node.Ref
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue

@ExperimentalFoundationApi
@ExperimentalFocus
@Composable
fun TextInput(
    autoFocus: Boolean = false,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = AmbientTextStyle.current,
    placeholder: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    alignment: Alignment = Alignment.TopStart,
    imeAction: ImeAction = ImeAction.Unspecified,
    onImeActionPerformed: (ImeAction, SoftwareKeyboardController?) -> Unit = { _, _ -> },
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onTextInputStarted: ((SoftwareKeyboardController) -> Unit)? = null,
    onClicked: (() -> Unit)? = null,
) {
    val focusRequester = FocusRequester()
    val keyboardController = remember { Ref<SoftwareKeyboardController>() }
    val interactionState = remember { InteractionState() }
    if (autoFocus) {
        onActive {
            focusRequester.requestFocus()
            keyboardController.value?.showSoftwareKeyboard()
        }
    }

    Box(
        modifier = modifier
            .focusRequester(focusRequester)
            .fillMaxSize()
            .clickable(interactionState = interactionState, indication = null) {
                onClicked?.invoke()
                focusRequester.requestFocus()
                // TODO(b/163109449): Showing and hiding keyboard should be handled by BaseTextField.
                //  The requestFocus() call here should be enough to trigger the software keyboard.
                //  Investiate why this is needed here. If it is really needed, instead of doing
                //  this in the onClick callback, we should move this logic to the focusObserver
                //  so that it can show or hide the keyboard based on the focus state.
                keyboardController.value?.showSoftwareKeyboard()
            },
        alignment = alignment,
    ) {
        BaseTextField(
            textStyle = textStyle,
            keyboardType = keyboardType,
            imeAction = imeAction,
            onImeActionPerformed = {
                onImeActionPerformed(it, keyboardController.value)
            },
            onTextInputStarted = {
                keyboardController.value = it
                onTextInputStarted?.invoke(it)
            },
            value = value,
            onValueChange = onValueChange,
        )
        if (value.text.isEmpty()) {
            ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                placeholder?.invoke()
            }
        }
    }
}
