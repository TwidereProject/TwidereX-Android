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
package com.twidere.twiderex.icon.twidereicons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.icon.TwidereIcons

public val TwidereIcons.ChooseToUse: ImageVector
  get() {
    if (_chooseToUse != null) {
      return _chooseToUse!!
    }
    _chooseToUse = Builder(
      name = "ChooseToUse",
      defaultWidth = 24.0.dp,
      defaultHeight =
      24.0.dp,
      viewportWidth = 24.0f,
      viewportHeight = 24.0f
    ).apply {
      group {
        path(
          fill = SolidColor(Color(0x00000000)),
          stroke = SolidColor(Color(0xFF4C9EEB)),
          strokeLineWidth = 2.0f,
          strokeLineCap = Round,
          strokeLineJoin =
          StrokeJoin.Companion.Round,
          strokeLineMiter = 4.0f,
          pathFillType =
          NonZero
        ) {
          moveTo(18.0f, 4.0f)
          horizontalLineTo(6.0f)
          curveTo(4.8954f, 4.0f, 4.0f, 4.8954f, 4.0f, 6.0f)
          verticalLineTo(18.0f)
          curveTo(4.0f, 19.1046f, 4.8954f, 20.0f, 6.0f, 20.0f)
          horizontalLineTo(18.0f)
          curveTo(19.1046f, 20.0f, 20.0f, 19.1046f, 20.0f, 18.0f)
          verticalLineTo(6.0f)
          curveTo(20.0f, 4.8954f, 19.1046f, 4.0f, 18.0f, 4.0f)
          close()
        }
        path(
          fill = SolidColor(Color(0x00000000)),
          stroke = SolidColor(Color(0xFF4C9EEB)),
          strokeLineWidth = 2.0f,
          strokeLineCap = Round,
          strokeLineJoin =
          StrokeJoin.Companion.Round,
          strokeLineMiter = 4.0f,
          pathFillType =
          NonZero
        ) {
          moveTo(4.0f, 18.0f)
          horizontalLineTo(6.0f)
          curveTo(7.5913f, 18.0f, 9.1174f, 17.3679f, 10.2426f, 16.2426f)
          curveTo(11.3679f, 15.1174f, 12.0f, 13.5913f, 12.0f, 12.0f)
          curveTo(12.0f, 10.4087f, 12.6321f, 8.8826f, 13.7574f, 7.7574f)
          curveTo(14.8826f, 6.6321f, 16.4087f, 6.0f, 18.0f, 6.0f)
          horizontalLineTo(20.0f)
        }
      }
    }
      .build()
    return _chooseToUse!!
  }

private var _chooseToUse: ImageVector? = null
