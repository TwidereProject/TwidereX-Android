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
package com.twidere.services.serializer

import com.twidere.services.utils.DateFormatUtils
import kotlin.test.Test
import kotlin.test.assertNotNull

class DateSerializerTest {

  @Test
  fun test() {
    assertNotNull(DateFormatUtils.parse("2019-04-12T16:38:15"))
    assertNotNull(DateFormatUtils.parse("2019-04-12T16:38:15Z"))
    assertNotNull(DateFormatUtils.parse("2019-04-12T16:38:15.123"))
    assertNotNull(DateFormatUtils.parse("2022-05-05T06:05:28.000Z"))
    assertNotNull(DateFormatUtils.parse("Thu May 05 06:29:56 +0000 2022"))
    assertNotNull(DateFormatUtils.parse("Dec 2, 2017 8:45 AM UTC"))
    assertNotNull(DateFormatUtils.parse("Dec 2, 2017·8:45 AM UTC"))
    assertNotNull(DateFormatUtils.parse("Dec 2, 2017 · 8:45 AM UTC"))
  }
}
