package com.twidere.twiderex.component

import androidx.compose.foundation.BaseTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.node.Ref
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue

@ExperimentalFoundationApi
@ExperimentalFocus
@Composable
fun TextInput(
    autoFocus: Boolean = false,
    modifier: Modifier = Modifier,
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
    ) {
        BaseTextField(
            onTextInputStarted = {
                keyboardController.value = it
                onTextInputStarted?.invoke(it)
            },
            value = value,
            onValueChange = onValueChange,
        )
    }

}