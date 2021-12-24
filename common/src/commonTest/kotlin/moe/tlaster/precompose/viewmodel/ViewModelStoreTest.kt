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

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ViewModelStoreTest {
    @Test
    fun testClear() {
        val store = ViewModelStore()
        val viewModel1 = TestViewModel()
        val viewModel2 = TestViewModel()
        val mockViewModel = TestViewModel()
        store.put("a", viewModel1)
        store.put("b", viewModel2)
        store.put("mock", mockViewModel)
        assertFalse(viewModel1.cleared)
        assertFalse(viewModel2.cleared)
        store.clear()
        assertTrue(viewModel1.cleared)
        assertTrue(viewModel2.cleared)
        assertNull(store["a"])
        assertNull(store["b"])
    }

    internal class TestViewModel : ViewModel() {
        var cleared = false
        override fun onCleared() {
            cleared = true
        }
    }
}
