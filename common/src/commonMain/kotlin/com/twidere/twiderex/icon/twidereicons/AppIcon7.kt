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
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.icon.TwidereIcons

public val TwidereIcons.AppIcon7: ImageVector
  get() {
    if (_appIcon7 != null) {
      return _appIcon7!!
    }
    _appIcon7 = Builder(
      name = "AppIcon7",
      defaultWidth = 512.0.dp,
      defaultHeight = 512.0.dp,
      viewportWidth = 512.0f,
      viewportHeight = 512.0f
    ).apply {
      group {
        path(
          fill = SolidColor(Color(0xFF9ACB1E)),
          stroke = null,
          strokeLineWidth = 0.0f,
          strokeLineCap = Butt,
          strokeLineJoin = Miter,
          strokeLineMiter = 4.0f,
          pathFillType = NonZero
        ) {
          moveTo(512.0f, 0.0f)
          horizontalLineTo(0.0f)
          verticalLineTo(512.0f)
          horizontalLineTo(512.0f)
          verticalLineTo(0.0f)
          close()
        }
        path(
          fill = SolidColor(Color(0xFF000000)),
          stroke = null,
          fillAlpha = 0.75f,
          strokeLineWidth = 0.0f,
          strokeLineCap = Butt,
          strokeLineJoin = Miter,
          strokeLineMiter = 4.0f,
          pathFillType = NonZero
        ) {
          moveTo(216.757f, 241.724f)
          curveTo(219.101f, 239.381f, 222.9f, 239.381f, 225.243f, 241.724f)
          lineTo(342.272f, 358.753f)
          curveTo(343.398f, 359.879f, 344.03f, 361.405f, 344.03f, 362.996f)
          curveTo(344.03f, 366.31f, 341.343f, 368.996f, 338.03f, 368.996f)
          horizontalLineTo(221.0f)
          curveTo(217.686f, 368.996f, 215.0f, 366.31f, 215.0f, 362.996f)
          verticalLineTo(245.966f)
          curveTo(215.0f, 244.375f, 215.632f, 242.849f, 216.757f, 241.724f)
          close()
          moveTo(197.0f, 240.23f)
          curveTo(200.314f, 240.23f, 203.0f, 242.916f, 203.0f, 246.23f)
          verticalLineTo(345.761f)
          curveTo(203.0f, 347.366f, 202.357f, 348.904f, 201.216f, 350.031f)
          curveTo(198.858f, 352.359f, 195.059f, 352.335f, 192.73f, 349.977f)
          lineTo(143.594f, 300.212f)
          curveTo(141.287f, 297.875f, 141.287f, 294.117f, 143.594f, 291.78f)
          lineTo(192.73f, 242.014f)
          curveTo(193.858f, 240.872f, 195.395f, 240.23f, 197.0f, 240.23f)
          close()
          moveTo(403.475f, 222.996f)
          curveTo(405.066f, 222.996f, 406.592f, 223.628f, 407.717f, 224.753f)
          curveTo(410.06f, 227.097f, 410.06f, 230.896f, 407.717f, 233.239f)
          lineTo(324.966f, 315.99f)
          curveTo(322.623f, 318.333f, 318.824f, 318.333f, 316.48f, 315.99f)
          lineTo(233.729f, 233.239f)
          curveTo(232.603f, 232.113f, 231.971f, 230.587f, 231.971f, 228.996f)
          curveTo(231.971f, 225.682f, 234.658f, 222.996f, 237.971f, 222.996f)
          horizontalLineTo(403.475f)
          close()
          moveTo(138.785f, 215.632f)
          curveTo(141.143f, 213.304f, 144.942f, 213.328f, 147.27f, 215.686f)
          lineTo(170.409f, 239.12f)
          curveTo(172.716f, 241.456f, 172.716f, 245.214f, 170.409f, 247.551f)
          lineTo(147.271f, 270.986f)
          curveTo(146.144f, 272.128f, 144.606f, 272.771f, 143.002f, 272.771f)
          curveTo(139.688f, 272.771f, 137.002f, 270.085f, 137.002f, 266.771f)
          lineTo(137.0f, 219.902f)
          curveTo(137.0f, 218.297f, 137.643f, 216.759f, 138.785f, 215.632f)
          close()
          moveTo(178.783f, 150.809f)
          curveTo(181.141f, 148.481f, 184.94f, 148.505f, 187.269f, 150.863f)
          lineTo(222.409f, 186.45f)
          curveTo(224.716f, 188.786f, 224.716f, 192.544f, 222.409f, 194.881f)
          lineTo(187.272f, 230.471f)
          lineTo(187.217f, 230.526f)
          curveTo(184.859f, 232.854f, 181.06f, 232.829f, 178.732f, 230.471f)
          lineTo(143.593f, 194.881f)
          curveTo(141.286f, 192.544f, 141.286f, 188.786f, 143.593f, 186.45f)
          lineTo(178.73f, 150.863f)
          lineTo(178.783f, 150.809f)
          close()
          moveTo(154.291f, 144.0f)
          curveTo(155.869f, 144.0f, 157.384f, 144.622f, 158.507f, 145.73f)
          curveTo(160.865f, 148.059f, 160.889f, 151.858f, 158.561f, 154.216f)
          lineTo(135.27f, 177.805f)
          lineTo(135.216f, 177.859f)
          curveTo(132.858f, 180.187f, 129.059f, 180.163f, 126.73f, 177.805f)
          lineTo(103.441f, 154.215f)
          curveTo(102.332f, 153.093f, 101.71f, 151.578f, 101.71f, 150.0f)
          curveTo(101.71f, 146.686f, 104.397f, 144.0f, 107.71f, 144.0f)
          horizontalLineTo(154.291f)
          close()
        }
      }
    }
      .build()
    return _appIcon7!!
  }

private var _appIcon7: ImageVector? = null
