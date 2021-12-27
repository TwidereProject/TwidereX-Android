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
package com.twidere.twiderex.init

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class Initializer private constructor(private val scope: CoroutineScope) {
    private val tasks = mutableListOf<InitialTask>()
    private val asyncTasks = mutableListOf<AsyncInitialTask>()
    companion object {
        fun withScope(scope: CoroutineScope) = Initializer(scope)
    }
    fun add(task: InitialTask): Initializer {
        tasks.add(task)
        return this
    }

    fun add(task: AsyncInitialTask): Initializer {
        asyncTasks.add(task)
        return this
    }

    fun execute() {
        tasks.forEach {
            it.execute()
        }
        scope.launch {
            asyncTasks.forEach {
                it.execute()
            }
        }
    }
}

internal interface InitialTask {
    fun execute()
}

internal interface AsyncInitialTask {
    suspend fun execute()
}
