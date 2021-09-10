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

import androidx.activity.ComponentActivity
import androidx.savedstate.SavedStateRegistryOwner
import moe.tlaster.precompose.ui.BackDispatcher
import moe.tlaster.precompose.ui.BackDispatcherOwner
import moe.tlaster.precompose.viewmodel.ViewModelStore
import moe.tlaster.precompose.viewmodel.ViewModelStoreOwner

open class PreComposeActivity :
    ComponentActivity(),
    LifecycleOwner,
    ViewModelStoreOwner,
    androidx.lifecycle.LifecycleOwner,
    SavedStateRegistryOwner,
    BackDispatcherOwner {
    override val lifecycle by lazy {
        LifecycleRegistry()
    }

    override val viewModelStore by lazy {
        ViewModelStore()
    }

    override fun onResume() {
        super.onResume()
        lifecycle.currentState = Lifecycle.State.Active
    }

    override fun onPause() {
        super.onPause()
        lifecycle.currentState = Lifecycle.State.InActive
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.currentState = Lifecycle.State.Destroyed
    }

    override val backDispatcher by lazy {
        BackDispatcher()
    }

    override fun onBackPressed() {
        if (!backDispatcher.onBackPress()) {
            super.onBackPressed()
        }
    }
}
