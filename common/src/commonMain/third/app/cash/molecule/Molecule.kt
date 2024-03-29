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

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart.UNDISPATCHED
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Create a [Flow] which will continually recompose `body` to produce a stream of [T] values
 * when collected.
 */
fun <T> moleculeFlow(clock: RecompositionClock, body: @Composable () -> T): Flow<T> {
  return when (clock) {
    RecompositionClock.ContextClock -> contextClockFlow(body)
    RecompositionClock.Immediate -> immediateClockFlow(body)
  }
}

private fun <T> contextClockFlow(body: @Composable () -> T) = channelFlow {
  launchMolecule(
    clock = RecompositionClock.ContextClock,
    emitter = {
      trySend(it).getOrThrow()
    },
    body = body,
  )
}

private fun <T> immediateClockFlow(body: @Composable () -> T): Flow<T> = flow {
  coroutineScope {
    val clock = GatedFrameClock(this)
    val outputBuffer = Channel<T>(1)

    launch(clock, start = UNDISPATCHED) {
      launchMolecule(
        clock = RecompositionClock.ContextClock,
        emitter = {
          clock.isRunning = false
          outputBuffer.trySend(it).getOrThrow()
        },
        body = body,
      )
    }

    while (true) {
      val result = outputBuffer.tryReceive()
      // Per `ReceiveChannel.tryReceive` documentation: isFailure means channel is empty.
      val value = if (result.isFailure) {
        clock.isRunning = true
        outputBuffer.receive()
      } else {
        result.getOrThrow()
      }
      emit(value)
    }
    /*
    TODO: Replace the above block with the following once `ReceiveChannel.isEmpty` is stable:

    for (item in outputBuffer) {
      emit(item)
      if (outputBuffer.isEmpty) {
        clock.isRunning = true
      }
    }
    */
  }
}

/**
 * Launch a coroutine into this [CoroutineScope] which will continually recompose `body`
 * to produce a [StateFlow] stream of [T] values.
 */
fun <T> CoroutineScope.launchMolecule(
  clock: RecompositionClock,
  body: @Composable () -> T,
): StateFlow<T> {
  var flow: MutableStateFlow<T>? = null

  launchMolecule(
    clock = clock,
    emitter = { value ->
      val outputFlow = flow
      if (outputFlow != null) {
        outputFlow.value = value
      } else {
        flow = MutableStateFlow(value)
      }
    },
    body = body,
  )

  return flow!!
}

/**
 * Launch a coroutine into this [CoroutineScope] which will continually recompose `body`
 * to invoke [emitter] with each returned [T] value.
 *
 * [launchMolecule]'s [emitter] is always free-running and will not respect backpressure.
 * Use [moleculeFlow] to create a backpressure-capable flow.
 */
fun <T> CoroutineScope.launchMolecule(
  clock: RecompositionClock,
  emitter: (value: T) -> Unit,
  body: @Composable () -> T,
) {
  val clockContext = when (clock) {
    RecompositionClock.ContextClock -> EmptyCoroutineContext
    RecompositionClock.Immediate -> GatedFrameClock(this)
  }

  with(this + clockContext) {
    val recomposer = Recomposer(coroutineContext)
    val composition = Composition(UnitApplier, recomposer)
    launch(start = UNDISPATCHED) {
      recomposer.runRecomposeAndApplyChanges()
    }

    var applyScheduled = false
    val snapshotHandle = Snapshot.registerGlobalWriteObserver {
      if (!applyScheduled) {
        applyScheduled = true
        launch {
          applyScheduled = false
          Snapshot.sendApplyNotifications()
        }
      }
    }
    coroutineContext.job.invokeOnCompletion {
      composition.dispose()
      snapshotHandle.dispose()
    }

    composition.setContent {
      emitter(body())
    }
  }
}

private object UnitApplier : AbstractApplier<Unit>(Unit) {
  override fun insertBottomUp(index: Int, instance: Unit) {}
  override fun insertTopDown(index: Int, instance: Unit) {}
  override fun move(from: Int, to: Int, count: Int) {}
  override fun remove(index: Int, count: Int) {}
  override fun onClear() {}
}
