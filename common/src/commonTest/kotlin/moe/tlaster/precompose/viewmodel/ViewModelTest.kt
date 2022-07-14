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
package moe.tlaster.precompose.viewmodel

import java.io.Closeable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ViewModelTest {
    internal class CloseableImpl : Closeable {
        var wasDisposable = false

        override fun close() {
            wasDisposable = true
        }
    }

    internal class ViewModel : moe.tlaster.precompose.viewmodel.ViewModel()

    @Test
    fun testCloseableTag() {
        val vm = ViewModel()
        val impl = CloseableImpl()
        vm.setTagIfAbsent<Any>("totally_not_coroutine_context", impl)
        vm.clear()
        assertTrue(impl.wasDisposable)
    }

    @Test
    fun testCloseableTagAlreadyClearedVM() {
        val vm = ViewModel()
        vm.clear()
        val impl = CloseableImpl()
        vm.setTagIfAbsent<Any>("key", impl)
        assertTrue(impl.wasDisposable)
    }

    @Test
    fun testAlreadyAssociatedKey() {
        val vm = ViewModel()
        assertEquals("first", vm.setTagIfAbsent("key", "first"))
        assertEquals("first", vm.setTagIfAbsent("key", "second"))
    }
}
