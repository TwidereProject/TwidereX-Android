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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val TwidereIcons.IcTranslate: ImageVector
  get() {
    if (_icTranslate != null) {
      return _icTranslate!!
    }
    _icTranslate = Builder(
      name = "IcTranslate",
      defaultWidth = 24.0.dp,
      defaultHeight =
      24.0.dp,
      viewportWidth = 24.0f,
      viewportHeight = 24.0f
    ).apply {
      path(
        fill = SolidColor(Color(0xFF000000)),
        stroke = null,
        strokeLineWidth = 0.0f,
        strokeLineCap = Butt,
        strokeLineJoin = Miter,
        strokeLineMiter = 4.0f,
        pathFillType = NonZero
      ) {
        moveTo(12.87f, 15.07f)
        lineToRelative(-2.54f, -2.51f)
        lineToRelative(0.03f, -0.03f)
        curveToRelative(1.74f, -1.94f, 2.98f, -4.17f, 3.71f, -6.53f)
        lineTo(17.0f, 6.0f)
        lineTo(17.0f, 4.0f)
        horizontalLineToRelative(-7.0f)
        lineTo(10.0f, 2.0f)
        lineTo(8.0f, 2.0f)
        verticalLineToRelative(2.0f)
        lineTo(1.0f, 4.0f)
        verticalLineToRelative(1.99f)
        horizontalLineToRelative(11.17f)
        curveTo(11.5f, 7.92f, 10.44f, 9.75f, 9.0f, 11.35f)
        curveTo(8.07f, 10.32f, 7.3f, 9.19f, 6.69f, 8.0f)
        horizontalLineToRelative(-2.0f)
        curveToRelative(0.73f, 1.63f, 1.73f, 3.17f, 2.98f, 4.56f)
        lineToRelative(-5.09f, 5.02f)
        lineTo(4.0f, 19.0f)
        lineToRelative(5.0f, -5.0f)
        lineToRelative(3.11f, 3.11f)
        lineToRelative(0.76f, -2.04f)
        close()
        moveTo(18.5f, 10.0f)
        horizontalLineToRelative(-2.0f)
        lineTo(12.0f, 22.0f)
        horizontalLineToRelative(2.0f)
        lineToRelative(1.12f, -3.0f)
        horizontalLineToRelative(4.75f)
        lineTo(21.0f, 22.0f)
        horizontalLineToRelative(2.0f)
        lineToRelative(-4.5f, -12.0f)
        close()
        moveTo(15.88f, 17.0f)
        lineToRelative(1.62f, -4.33f)
        lineTo(19.12f, 17.0f)
        horizontalLineToRelative(-3.24f)
        close()
      }
    }
      .build()
    return _icTranslate!!
  }

private var _icTranslate: ImageVector? = null
