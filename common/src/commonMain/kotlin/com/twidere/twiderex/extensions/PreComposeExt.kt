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
package com.twidere.twiderex.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import app.cash.molecule.RecompositionClock
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import moe.tlaster.precompose.ui.viewModel
import moe.tlaster.precompose.viewmodel.ViewModel
import kotlin.coroutines.CoroutineContext

internal expect fun providePlatformDispatcher(): CoroutineContext

private class PresenterViewModel<T>(
  body: @Composable () -> T,
) : ViewModel() {
  private val dispatcher = providePlatformDispatcher()
  private val clock = if (dispatcher[MonotonicFrameClock] == null) {
    RecompositionClock.Immediate
  } else {
    RecompositionClock.ContextClock
  }
  private val scope = CoroutineScope(dispatcher)
  val state = scope.launchMolecule(clock, body)

  override fun onCleared() {
    scope.cancel()
  }
}

@Composable
fun <T> rememberPresenter(body: @Composable () -> T): StateFlow<T> {
  @Suppress("UNCHECKED_CAST")
  val viewModel = viewModel(
    modelClass = PresenterViewModel::class,
    keys = listOf(
      currentCompositeKeyHash.toString(36),
    ),
    creator = { PresenterViewModel(body) }
  ) as PresenterViewModel<T>
  return viewModel.state
}

private class EventViewModel<T> : ViewModel() {
  val channel = Channel<T>(capacity = Channel.BUFFERED)
  val pair = channel to channel.consumeAsFlow()
}

@Composable
fun <E> rememberEvent(): Pair<Channel<E>, Flow<E>> {
  @Suppress("UNCHECKED_CAST")
  val viewModel = viewModel(
    modelClass = EventViewModel::class,
    keys = listOf(
      currentCompositeKeyHash.toString(36),
    ),
    creator = { EventViewModel<E>() }
  ) as EventViewModel<E>
  return viewModel.pair
}

@Composable
fun <T, E> rememberPresenterState(
  body: @Composable (flow: Flow<E>) -> T
): Pair<T, Channel<E>> {
  val (channel, event) = rememberEvent<E>()
  val presenter = rememberPresenter { body(event) }
  val state by presenter.collectAsState()
  return state to channel
}

@Composable
fun <T, E> rememberNestedPresenter(
  body: @Composable (flow: Flow<E>) -> T
): Pair<T, Channel<E>> {
  val channel = remember { Channel<E>(capacity = Channel.BUFFERED) }
  val flow = remember { channel.consumeAsFlow() }
  val presenter = body(flow)
  return presenter to channel
}

@Composable
fun <T> Flow<T>.collectEvent(
  body: suspend T.() -> Unit,
) {
  LaunchedEffect(Unit) {
    collect {
      body(it)
    }
  }
}
