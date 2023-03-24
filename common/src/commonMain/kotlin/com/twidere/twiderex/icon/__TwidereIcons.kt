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
package com.twidere.twiderex.icon

import androidx.compose.ui.graphics.vector.ImageVector
import com.twidere.twiderex.icon.twidereicons.AppIcon1
import com.twidere.twiderex.icon.twidereicons.AppIcon10
import com.twidere.twiderex.icon.twidereicons.AppIcon11
import com.twidere.twiderex.icon.twidereicons.AppIcon12
import com.twidere.twiderex.icon.twidereicons.AppIcon13
import com.twidere.twiderex.icon.twidereicons.AppIcon14
import com.twidere.twiderex.icon.twidereicons.AppIcon2
import com.twidere.twiderex.icon.twidereicons.AppIcon3
import com.twidere.twiderex.icon.twidereicons.AppIcon4
import com.twidere.twiderex.icon.twidereicons.AppIcon5
import com.twidere.twiderex.icon.twidereicons.AppIcon6
import com.twidere.twiderex.icon.twidereicons.AppIcon7
import com.twidere.twiderex.icon.twidereicons.AppIcon8
import com.twidere.twiderex.icon.twidereicons.AppIcon9
import com.twidere.twiderex.icon.twidereicons.ChooseToUse
import kotlin.collections.List as ____KtList

public object TwidereIcons

private var __AllIcons: ____KtList<ImageVector>? = null

public val TwidereIcons.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons = listOf(
      AppIcon7, AppIcon6, AppIcon4, AppIcon14, AppIcon5, AppIcon1, AppIcon10,
      AppIcon11, AppIcon2, AppIcon13, AppIcon12, AppIcon3, ChooseToUse, AppIcon8, AppIcon9
    )
    return __AllIcons!!
  }
