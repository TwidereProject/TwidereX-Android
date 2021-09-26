package com.twidere.twiderex.db.sqldelight.query

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.internal.copyOnWriteList

internal fun<T:Any, R:Any> Query<T>.flatMap(map: (T) -> R):Query<R> {
    return object : Query<R>(
        queries = copyOnWriteList(),
        mapper = {
            val db = mapper.invoke(it)
            map.invoke(db)
        }
    ) {
        override fun execute() = this@flatMap.execute()
    }
}