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
package com.twidere.services.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class PKCETest {

    @Test
    fun test_ver() {
        val codeVerifier = "9x3Zy~DQ4sRnM9_zSHcSjJxMcnSmMM2f_-TeI2E.o4Q1pUhZ5bvZSb~v4dEj1d5H8bz8z8qxmRrPNDVevOWvbXG-1Run-FT3c3rJ6sCotHq~rRYwshKFB02Fi9WsE0wc"
        val codeChallenge = generateCodeChallenge(codeVerifier)
        assertEquals(
            "xFDtMv6PnmlIMvWK4u4j4tkY98py92wVacWiisXxJ1E",
            codeChallenge,
        )
    }
}
