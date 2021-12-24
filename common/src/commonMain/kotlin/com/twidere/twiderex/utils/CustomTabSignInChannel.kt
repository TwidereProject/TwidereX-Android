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
package com.twidere.twiderex.utils

import com.twidere.twiderex.navigation.RootDeepLinks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel

object CustomTabSignInChannel {
    private var waiting = false
    private val channel: Channel<String> = Channel()

    suspend fun send(uri: String) {
        if (waiting) {
            channel.send(uri)
        }
        waiting = false
    }

    fun canHandle(uri: String): Boolean {
        return uri.startsWith(RootDeepLinks.Callback.SignIn.Mastodon) ||
            uri.startsWith(RootDeepLinks.Callback.SignIn.Twitter)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun waitOne(): String {
        waiting = true
        return channel.receive()
    }

    fun onClose() {
        if (waiting) {
            channel.close()
            waiting = false
        }
    }
}
