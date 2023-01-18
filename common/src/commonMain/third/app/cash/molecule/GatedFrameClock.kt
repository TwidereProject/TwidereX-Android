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
package app.cash.molecule

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.MonotonicFrameClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.launch

/**
 * A [MonotonicFrameClock] that is either running, or not.
 *
 * While running, any request for a frame immediately succeeds. If stopped, requests for a frame wait until
 * the clock is set to run again.
 */
internal class GatedFrameClock(scope: CoroutineScope) : MonotonicFrameClock {
  private val frameSends = Channel<Unit>(CONFLATED)

  init {
    scope.launch {
      for (send in frameSends) sendFrame()
    }
  }

  var isRunning: Boolean = true
    set(value) {
      val started = value && !field
      field = value
      if (started) {
        sendFrame()
      }
    }

  private fun sendFrame() {
    clock.sendFrame(0L)
  }

  private val clock = BroadcastFrameClock {
    frameSends.trySend(Unit).getOrThrow()
  }

  override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R {
    return clock.withFrameNanos(onFrame)
  }
}
