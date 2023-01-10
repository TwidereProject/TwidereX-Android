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
import kotlinx.coroutines.channels.Channel

object BrowserLoginDeepLinksChannel {
  private val channel: Channel<String> = Channel()

  fun send(uri: String) {
    channel.trySend(uri)
  }

  fun canHandle(uri: String): Boolean {
    return uri.startsWith(RootDeepLinks.Callback.SignIn.Mastodon) ||
      uri.startsWith(RootDeepLinks.Callback.SignIn.Twitter)
  }

  suspend fun waitOne(): String {
    return channel.receive()
  }

  fun cancel() {
    channel.cancel()
  }
}
