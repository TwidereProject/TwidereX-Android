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
package moe.tlaster.precompose.lifecycle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

suspend fun Lifecycle.repeatOnLifecycle(
    block: suspend CoroutineScope.() -> Unit
) {
    if (currentState === Lifecycle.State.Destroyed) {
        return
    }
    coroutineScope {
        withContext(Dispatchers.Main.immediate) {
            if (currentState === Lifecycle.State.Destroyed) return@withContext
            var launchedJob: Job? = null
            var observer: LifecycleObserver? = null
            try {
                suspendCancellableCoroutine<Unit> { cont ->
                    object : LifecycleObserver {
                        override fun onStateChanged(state: Lifecycle.State) {
                            when (state) {
                                Lifecycle.State.Initialized -> Unit
                                Lifecycle.State.Active -> {
                                    launchedJob = this@coroutineScope.launch(block = block)
                                }
                                Lifecycle.State.InActive -> {
                                    launchedJob?.cancel()
                                    launchedJob = null
                                }
                                Lifecycle.State.Destroyed -> {
                                    cont.resume(Unit)
                                }
                            }
                        }
                    }.let {
                        observer = it
                        this@repeatOnLifecycle.addObserver(it)
                    }
                }
            } finally {
                launchedJob?.cancel()
                observer?.let {
                    this@repeatOnLifecycle.removeObserver(it)
                }
            }
        }
    }
}
