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
package moe.tlaster.precompose.navigation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import moe.tlaster.precompose.navigation.transition.NavTransition

@Stable
internal class RouteStack(
    val id: Long,
    val scene: BackStackEntry,
    val dialogStack: SnapshotStateList<BackStackEntry> = mutableStateListOf(),
    val navTransition: NavTransition? = null,
) : LifecycleOwner {
    private var destroyAfterTransition = false
    val currentEntry: BackStackEntry
        get() = if (dialogStack.any()) {
            dialogStack.last()
        } else {
            scene
        }
    val currentDialogStack: BackStackEntry?
        get() = dialogStack.lastOrNull()

    private val lifecycleRegistry by lazy {
        LifecycleRegistry(this)
    }

    val canGoBack: Boolean
        get() = dialogStack.isNotEmpty()

    fun goBack(): BackStackEntry {
        return dialogStack.removeLast().apply {
            viewModelStore.clear()
        }
    }

    fun onActive() {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    fun onInActive() {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        if (destroyAfterTransition) {
            onDestroyed()
        }
    }

    fun onDestroyed() {
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            destroyAfterTransition = true
        } else {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
            dialogStack.forEach {
                it.viewModelStore.clear()
            }
            scene.viewModelStore.clear()
        }
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}
