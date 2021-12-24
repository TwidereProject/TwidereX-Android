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
package com.twidere.twiderex.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.model.enums.MastodonVisibility

@Composable
fun MastodonVisibility.icon(): Painter {
    return when (this) {
        MastodonVisibility.Public -> painterResource(res = com.twidere.twiderex.MR.files.ic_globe)
        MastodonVisibility.Unlisted -> painterResource(res = com.twidere.twiderex.MR.files.ic_lock_open)
        MastodonVisibility.Private -> painterResource(res = com.twidere.twiderex.MR.files.ic_lock)
        MastodonVisibility.Direct -> painterResource(res = com.twidere.twiderex.MR.files.ic_mail)
    }
}

@Composable
fun MastodonVisibility.stringName(): String {
    return when (this) {
        MastodonVisibility.Public -> stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_visibility_public)
        MastodonVisibility.Unlisted -> stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_visibility_unlisted)
        MastodonVisibility.Private -> stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_visibility_private)
        MastodonVisibility.Direct -> stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_visibility_direct)
    }
}
