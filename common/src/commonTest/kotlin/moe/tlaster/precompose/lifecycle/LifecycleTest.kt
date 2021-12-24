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
package moe.tlaster.precompose.lifecycle

import kotlin.test.Test
import kotlin.test.assertEquals

class LifecycleTest {
    @Test
    fun lifeCycleTest() {
        val lifecycleOwner = TestLifecycleOwner()
        assertEquals(Lifecycle.State.Initialized, lifecycleOwner.lifecycle.currentState)
        lifecycleOwner.lifecycle.currentState = Lifecycle.State.Active
        assertEquals(Lifecycle.State.Active, lifecycleOwner.lifecycle.currentState)
        lifecycleOwner.lifecycle.currentState = Lifecycle.State.Initialized
        assertEquals(Lifecycle.State.Active, lifecycleOwner.lifecycle.currentState)
        lifecycleOwner.lifecycle.currentState = Lifecycle.State.InActive
        assertEquals(Lifecycle.State.InActive, lifecycleOwner.lifecycle.currentState)
        lifecycleOwner.lifecycle.currentState = Lifecycle.State.Active
        assertEquals(Lifecycle.State.Active, lifecycleOwner.lifecycle.currentState)
        lifecycleOwner.lifecycle.currentState = Lifecycle.State.Destroyed
        assertEquals(Lifecycle.State.Destroyed, lifecycleOwner.lifecycle.currentState)
        lifecycleOwner.lifecycle.currentState = Lifecycle.State.Active
        assertEquals(Lifecycle.State.Destroyed, lifecycleOwner.lifecycle.currentState)
    }
}
