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
package com.twidere.twiderex.db.sqldelight.query

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.internal.copyOnWriteList

internal fun <T : Any, R : Any> Query<T>.flatMap(map: (T) -> R): Query<R> {
    return object :
        Query<R>(
            queries = copyOnWriteList(),
            mapper = {
                val db = mapper.invoke(it)
                map.invoke(db)
            }
        ),
        Query.Listener {
        init {
            // flatMap should also dispatch previous query's events
            this@flatMap.addListener(this)
        }
        override fun execute() = this@flatMap.execute()

        override fun queryResultsChanged() {
            notifyDataChanged()
        }
    }
}
