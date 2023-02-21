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
package com.twidere.twiderex.kmp

import com.twidere.twiderex.icon.TwidereIcons
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

const val QUALIFIER = "com.twidere.twiderex"

expect class IconModifier {
  fun changeIcon(
    newIcon: AppIcon,
  )
}

val launchIcons = listOf(
  TwidereIcons.AppIcon1,
  TwidereIcons.AppIcon2,
  TwidereIcons.AppIcon3,
  TwidereIcons.AppIcon4,
  TwidereIcons.AppIcon5,
  TwidereIcons.AppIcon6,
  TwidereIcons.AppIcon7,
  TwidereIcons.AppIcon8,
  TwidereIcons.AppIcon9,
  TwidereIcons.AppIcon10,
  TwidereIcons.AppIcon11,
  TwidereIcons.AppIcon12,
  TwidereIcons.AppIcon13,
  TwidereIcons.AppIcon14,
)

enum class AppIcon(
  val componentName: String, // Must correspond to the <activity-alias> `android:name`s in AndroidManifest
) {
  DEFAULT(
    componentName = "$QUALIFIER.Launcher",
  ),
  Launcher1(
    componentName = "$QUALIFIER.Launcher1",
  ),
  Launcher2(
    componentName = "$QUALIFIER.Launcher2",
  ),
  Launcher3(
    componentName = "$QUALIFIER.Launcher3",
  ),
  Launcher4(
    componentName = "$QUALIFIER.Launcher4",
  ),
  Launcher5(
    componentName = "$QUALIFIER.Launcher5",
  ),
  Launcher6(
    componentName = "$QUALIFIER.Launcher6",
  ),
  Launcher7(
    componentName = "$QUALIFIER.Launcher7",
  ),
  Launcher8(
    componentName = "$QUALIFIER.Launcher8",
  ),
  Launcher9(
    componentName = "$QUALIFIER.Launcher9",
  ),
  Launcher10(
    componentName = "$QUALIFIER.Launcher10",
  ),
  Launcher11(
    componentName = "$QUALIFIER.Launcher11",
  ),
  Launcher12(
    componentName = "$QUALIFIER.Launcher12",
  ),
  Launcher13(
    componentName = "$QUALIFIER.Launcher13",
  ),
  ;

  companion object {
    fun fromValue(componentName: String): AppIcon {
      return values().first { it.componentName == componentName }
    }
    fun fromIndex(index: Int): AppIcon {
      return values()[index]
    }
  }
}
