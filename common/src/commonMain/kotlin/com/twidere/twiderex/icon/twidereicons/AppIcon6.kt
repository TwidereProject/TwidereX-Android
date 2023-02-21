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

public val TwidereIcons.AppIcon6: ImageVector
  get() {
    if (_appIcon6 != null) {
      return _appIcon6!!
    }
    _appIcon6 = Builder(
      name = "AppIcon6",
      defaultWidth = 512.0.dp,
      defaultHeight = 512.0.dp,
      viewportWidth = 512.0f,
      viewportHeight = 512.0f
    ).apply {
      group {
        path(
          fill = SolidColor(Color(0xFF38D29B)),
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
          fill = SolidColor(Color(0xFFffffff)),
          stroke = null,
          fillAlpha = 0.7f,
          strokeLineWidth = 0.0f,
          strokeLineCap = Butt,
          strokeLineJoin = Miter,
          strokeLineMiter = 4.0f,
          pathFillType = NonZero
        ) {
          moveTo(215.0f, 245.966f)
          verticalLineTo(362.996f)
          curveTo(215.0f, 366.31f, 217.686f, 368.996f, 221.0f, 368.996f)
          horizontalLineTo(338.03f)
          curveTo(341.343f, 368.996f, 344.03f, 366.31f, 344.03f, 362.996f)
          curveTo(344.03f, 361.405f, 343.398f, 359.879f, 342.272f, 358.753f)
          lineTo(225.243f, 241.724f)
          curveTo(222.899f, 239.381f, 219.101f, 239.381f, 216.757f, 241.724f)
          curveTo(215.632f, 242.849f, 215.0f, 244.375f, 215.0f, 245.966f)
          close()
        }
        path(
          fill = SolidColor(Color(0xFFffffff)),
          stroke = null,
          fillAlpha = 0.9f,
          strokeLineWidth = 0.0f,
          strokeLineCap = Butt,
          strokeLineJoin = Miter,
          strokeLineMiter = 4.0f,
          pathFillType = NonZero
        ) {
          moveTo(403.475f, 222.996f)
          horizontalLineTo(237.971f)
          curveTo(234.657f, 222.996f, 231.971f, 225.682f, 231.971f, 228.996f)
          curveTo(231.971f, 230.587f, 232.603f, 232.113f, 233.729f, 233.239f)
          lineTo(316.48f, 315.99f)
          curveTo(318.823f, 318.333f, 322.622f, 318.333f, 324.966f, 315.99f)
          lineTo(407.717f, 233.239f)
          curveTo(410.06f, 230.895f, 410.06f, 227.096f, 407.717f, 224.753f)
          curveTo(406.592f, 223.628f, 405.066f, 222.996f, 403.475f, 222.996f)
          close()
        }
        path(
          fill = SolidColor(Color(0xFFffffff)),
          stroke = null,
          fillAlpha = 0.8f,
          strokeLineWidth = 0.0f,
          strokeLineCap = Butt,
          strokeLineJoin = Miter,
          strokeLineMiter = 4.0f,
          pathFillType = NonZero
        ) {
          moveTo(192.73f, 242.014f)
          lineTo(143.594f, 291.78f)
          curveTo(141.287f, 294.117f, 141.287f, 297.875f, 143.594f, 300.212f)
          lineTo(192.73f, 349.977f)
          curveTo(195.059f, 352.335f, 198.858f, 352.359f, 201.216f, 350.031f)
          curveTo(202.357f, 348.904f, 203.0f, 347.366f, 203.0f, 345.761f)
          verticalLineTo(246.23f)
          curveTo(203.0f, 242.916f, 200.314f, 240.23f, 197.0f, 240.23f)
          curveTo(195.395f, 240.23f, 193.858f, 240.872f, 192.73f, 242.014f)
          close()
        }
        path(
          fill = SolidColor(Color(0xFFffffff)),
          stroke = null,
          fillAlpha = 0.8f,
          strokeLineWidth = 0.0f,
          strokeLineCap = Butt,
          strokeLineJoin = Miter,
          strokeLineMiter = 4.0f,
          pathFillType = NonZero
        ) {
          moveTo(178.73f, 150.863f)
          lineTo(143.593f, 186.45f)
          curveTo(141.286f, 188.786f, 141.286f, 192.544f, 143.593f, 194.881f)
          lineTo(178.732f, 230.471f)
          curveTo(181.06f, 232.829f, 184.859f, 232.854f, 187.217f, 230.526f)
          curveTo(187.236f, 230.508f, 187.254f, 230.49f, 187.272f, 230.471f)
          lineTo(222.409f, 194.881f)
          curveTo(224.716f, 192.544f, 224.716f, 188.786f, 222.409f, 186.45f)
          lineTo(187.269f, 150.863f)
          curveTo(184.94f, 148.505f, 181.141f, 148.481f, 178.784f, 150.809f)
          curveTo(178.765f, 150.827f, 178.748f, 150.845f, 178.73f, 150.863f)
          close()
        }
        path(
          fill = SolidColor(Color(0xFFffffff)),
          stroke = null,
          fillAlpha = 0.9f,
          strokeLineWidth = 0.0f,
          strokeLineCap = Butt,
          strokeLineJoin = Miter,
          strokeLineMiter = 4.0f,
          pathFillType = NonZero
        ) {
          moveTo(137.0f, 219.902f)
          lineTo(137.002f, 266.771f)
          curveTo(137.002f, 270.085f, 139.688f, 272.771f, 143.002f, 272.771f)
          curveTo(144.606f, 272.771f, 146.144f, 272.128f, 147.271f, 270.986f)
          lineTo(170.409f, 247.551f)
          curveTo(172.716f, 245.214f, 172.716f, 241.456f, 170.409f, 239.12f)
          lineTo(147.27f, 215.686f)
          curveTo(144.942f, 213.328f, 141.143f, 213.304f, 138.785f, 215.632f)
          curveTo(137.643f, 216.759f, 137.0f, 218.297f, 137.0f, 219.902f)
          close()
        }
        path(
          fill = SolidColor(Color(0xFFffffff)),
          stroke = null,
          fillAlpha = 0.9f,
          strokeLineWidth = 0.0f,
          strokeLineCap = Butt,
          strokeLineJoin = Miter,
          strokeLineMiter = 4.0f,
          pathFillType = NonZero
        ) {
          moveTo(154.291f, 144.0f)
          horizontalLineTo(107.71f)
          curveTo(104.397f, 144.0f, 101.71f, 146.686f, 101.71f, 150.0f)
          curveTo(101.71f, 151.578f, 102.332f, 153.093f, 103.441f, 154.215f)
          lineTo(126.73f, 177.805f)
          curveTo(129.059f, 180.163f, 132.858f, 180.187f, 135.216f, 177.859f)
          curveTo(135.234f, 177.841f, 135.252f, 177.823f, 135.27f, 177.805f)
          lineTo(158.561f, 154.216f)
          curveTo(160.889f, 151.858f, 160.865f, 148.059f, 158.507f, 145.73f)
          curveTo(157.384f, 144.622f, 155.869f, 144.0f, 154.291f, 144.0f)
          close()
        }
      }
    }
      .build()
    return _appIcon6!!
  }

private var _appIcon6: ImageVector? = null
