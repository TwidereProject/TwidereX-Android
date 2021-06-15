/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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

import android.net.Uri
import com.twidere.twiderex.scenes.mastodon.MASTODON_CALLBACK_URL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel

object CustomTabSignInChannel {
    private val channel: Channel<Uri> = Channel()

    suspend fun send(uri: Uri) {
        channel.send(uri)
    }

    fun canHandle(uri: Uri): Boolean {
        return uri.toString().startsWith(MASTODON_CALLBACK_URL)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun waitOne(): Uri {
        while (!channel.isEmpty) {
            channel.receive()
        }
        return channel.receive()
    }
}
