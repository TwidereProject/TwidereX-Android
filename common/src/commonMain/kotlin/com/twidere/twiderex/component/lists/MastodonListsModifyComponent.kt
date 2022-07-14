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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.component.foundation.AlertDialog
import com.twidere.twiderex.component.stringResource

@Composable
fun MastodonListsModifyComponent(
    onDismissRequest: () -> Unit,
    title: String,
    name: String,
    onNameChanged: (name: String) -> Unit,
    onConfirm: (name: String) -> Unit
) {
    // var name by remember { mutableStateOf(initName) }
    AlertDialog(
        onDismissRequest = {
            onDismissRequest.invoke()
        },

        text = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.subtitle1
                )
                Spacer(modifier = Modifier.padding(top = MastodonListsModifyComponentDefaults.VerticalPadding),)
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = name,
                    onValueChange = onNameChanged,
                    placeholder = {
                        Text(
                            text = stringResource(res = com.twidere.twiderex.MR.strings.scene_lists_modify_name),
                            style = MaterialTheme.typography.subtitle1,
                        )
                    },
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest.invoke()
                }
            ) {
                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(name)
                }
            ) {
                Text(text = stringResource(res = com.twidere.twiderex.MR.strings.common_controls_actions_ok))
            }
        },
        shape = RoundedCornerShape(0.dp)
    )
}

private object MastodonListsModifyComponentDefaults {
    val VerticalPadding = 20.dp
}
