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
package com.twidere.twiderex.preferences.model

import androidx.compose.runtime.Composable
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.dataprovider.mapper.Strings
import kotlinx.serialization.Serializable

@Serializable
data class AppearancePreferences(
  val primaryColorIndex: Int = 0,
  val tabPosition: TabPosition = TabPosition.Bottom,
  val theme: Theme = Theme.Auto,
  val hideTabBarWhenScroll: Boolean = false,
  val hideFabWhenScroll: Boolean = false,
  val hideAppBarWhenScroll: Boolean = false,
  val isDarkModePureBlack: Boolean = false,
  val windowInfo: WindowInfo = WindowInfo(),
  val tabToTop: TabToTop = TabToTop.SingleTap,
  val autoRefresh: Boolean = false,
  val autoRefreshInterval: RefreshInterval = RefreshInterval.OneMinute,
  val resetToTop: Boolean = false,
) {

  @Serializable
  data class WindowInfo(
    val top: Float = 50f,
    val start: Float = 50f,
    val width: Float = 400f,
    val height: Float = 800f,
  )

  @Serializable
  enum class TabPosition {
    Top,
    Bottom,
  }

  @Serializable
  enum class TabToTop {
    SingleTap,
    DoubleTap,
  }

  @Serializable
  enum class Theme {
    Auto,
    Light,
    Dark,
  }

  @Serializable
  enum class RefreshInterval(
    val duration: Long,
  ) {
    HalfMinute(30 * 1000),
    OneMinute(60 * 1000),
    TwoMinute(120 * 1000),
    FiveMinute(500 * 1000),
  }
}

@Composable
fun AppearancePreferences.RefreshInterval.toUi(): String {
  return when (this) {
    AppearancePreferences.RefreshInterval.HalfMinute -> {
      stringResource(res = Strings.scene_settings_behaviors_timeline_refreshing_section_refresh_interval_option_30_seconds)
    }
    AppearancePreferences.RefreshInterval.OneMinute -> {
      stringResource(res = Strings.scene_settings_behaviors_timeline_refreshing_section_refresh_interval_option_60_seconds)
    }
    AppearancePreferences.RefreshInterval.TwoMinute -> {
      stringResource(res = Strings.scene_settings_behaviors_timeline_refreshing_section_refresh_interval_option_120_seconds)
    }
    AppearancePreferences.RefreshInterval.FiveMinute -> {
      stringResource(res = Strings.scene_settings_behaviors_timeline_refreshing_section_refresh_interval_option_300_seconds)
    }
  }
}
