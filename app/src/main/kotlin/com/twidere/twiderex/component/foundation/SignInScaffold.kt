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

import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.component.LoginLogo
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.ui.standardPadding

@Composable
fun SignInScaffold(
    countAction: (count: Int) -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    var count by remember { mutableStateOf(0) }
    TwidereXTheme {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton(
                            icon = Icons.Default.Close
                        )
                    },
                    elevation = 0.dp,
                )
            }
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.align(Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LoginLogo(
                        modifier = Modifier
                            .size(with(AmbientDensity.current) { MaterialTheme.typography.h4.fontSize.toDp() })
                            .clickable(
                                indication = null,
                                interactionState = remember { InteractionState() },
                                onClick = {
                                    count++
                                    countAction.invoke(count)
                                },
                            )
                    )
                    Spacer(modifier = Modifier.width(standardPadding * 2))
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.h4,
                    )
                }
                Text(
                    modifier = Modifier
                        .weight(1F)
                        .align(Alignment.Start),
                    text = stringResource(id = R.string.scene_sign_in_hello_sign_in_to_get_started),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary,
                )

                content.invoke(this)

                Spacer(modifier = Modifier.height(standardPadding * 4))
            }
        }
    }
}
