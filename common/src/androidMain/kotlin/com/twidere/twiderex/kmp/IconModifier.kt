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

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

actual class IconModifier constructor(
  private val context: Context,
) {
  actual fun changeIcon(
    newIcon: AppIcon,
  ) {
    disable(context, newIcon)
    enable(context, newIcon)
  }

  private fun enable(
    context: Context,
    appIcon: AppIcon,
  ) {
    setComponentState(context, appIcon.componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
  }

  private fun disable(
    context: Context,
    appIcon: AppIcon,
  ) {
    AppIcon.values().filterNot { it.componentName == appIcon.componentName }.forEach {
      setComponentState(context, it.componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
    }
  }

  private fun setComponentState(
    context: Context,
    componentName: String,
    componentState: Int,
  ) {
    context.packageManager.setComponentEnabledSetting(
      ComponentName(context, componentName),
      componentState,
      PackageManager.DONT_KILL_APP,
    )
  }
}
