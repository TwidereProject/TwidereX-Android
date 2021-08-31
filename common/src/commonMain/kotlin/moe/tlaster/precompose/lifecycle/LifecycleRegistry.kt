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

class LifecycleRegistry : Lifecycle {
    private val observers = arrayListOf<LifecycleObserver>()
    override var currentState: Lifecycle.State = Lifecycle.State.Initialized
        set(value) {
            if (field == Lifecycle.State.Destroyed || value == Lifecycle.State.Initialized) {
                return
            }
            field = value
            dispatchState(value)
        }

    private fun dispatchState(value: Lifecycle.State) {
        observers.toMutableList().forEach {
            it.onStateChanged(value)
        }
    }

    override fun removeObserver(observer: LifecycleObserver) {
        observers.remove(observer)
    }

    override fun addObserver(observer: LifecycleObserver) {
        observers.add(observer)
    }

    override fun hasObserver(): Boolean {
        return observers.isNotEmpty()
    }
}