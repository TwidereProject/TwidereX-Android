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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope.Companion.align
import androidx.compose.foundation.layout.defaultMinSizeConstraints
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ButtonConstants
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoadingProgress() {
    CircularProgressIndicator(
        modifier = Modifier
            .defaultMinSizeConstraints(
                minHeight = ButtonConstants.DefaultMinHeight,
            )
            .padding(ButtonConstants.DefaultContentPadding)
            .align(Alignment.CenterHorizontally),
    )
}

fun LazyListScope.loading() {
    item {
        Box(
            modifier = Modifier.fillParentMaxWidth(),
            alignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .heightIn(min = ButtonConstants.DefaultMinHeight)
                    .padding(ButtonConstants.DefaultContentPadding),
            )
        }
    }
}