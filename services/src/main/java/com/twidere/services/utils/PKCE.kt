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

import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.random.Random
import kotlin.random.asKotlinRandom

typealias CodeVerifier = String

typealias CodeChallenge = String

private val stateAllowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
private val codeAllowedChars = stateAllowedChars + listOf('-', '_', '.', '~')

fun generateState(random: Random = SecureRandom().asKotlinRandom()): String {
    return buildString(10) {
        repeat(10) {
            append(stateAllowedChars.random(random))
        }
    }
}

fun generateCodeVerifier(random: Random = SecureRandom().asKotlinRandom()): String {
    val size = (43..128).random(random)
    return buildString(size) {
        repeat(size) {
            append(codeAllowedChars.random(random))
        }
    }
}

@Synchronized // MessageDigest is not thread-safe
fun generateCodeChallenge(verifier: CodeVerifier): CodeChallenge {
    val bytes = verifier.toByteArray(Charsets.US_ASCII)
    val messageDigest = MessageDigest.getInstance("SHA-256")
    messageDigest.update(bytes, 0, bytes.size)
    val digest = messageDigest.digest()
    return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP)
        .replace('+', '-')
        .replace('/', '_')
        .replace("=", "")
}
