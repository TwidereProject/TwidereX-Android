/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.worker

import androidx.work.Data
import androidx.work.ListenableWorker

fun successWorkDataOf(message: String? = null, data: Data? = null) =
    workData(success = true, message = message, pairs = data?.keyValueMap)

fun failureWorkDataOf(message: String, data: Data? = null) =
    workData(success = false, message = message, pairs = data?.keyValueMap)

fun successWorkDataOf(message: String? = null, vararg pairs: Pair<String, Any?>) =
    workData(success = true, message = message, pairs = pairs.toMap())

fun failureWorkDataOf(message: String, vararg pairs: Pair<String, Any?>) =
    workData(success = false, message = message, pairs = pairs.toMap())

private fun workData(
    success: Boolean,
    message: String? = null,
    pairs: Map<String, Any?>? = null
): Data {
    val builder = Data.Builder()
    if (pairs != null) {
        builder.putAll(pairs)
    }
    builder.putBoolean("success", success)
    builder.putString("message", message)
    return builder.build()
}

val Data.success: Boolean
    get() = getBoolean("success", false)

fun successResult(data: Data? = null) =
    ListenableWorker.Result.success(successWorkDataOf(data = data))

fun failureResult(message: String, data: Data? = null) =
    ListenableWorker.Result.success(failureWorkDataOf(message = message, data = data))

fun successResult(vararg pairs: Pair<String, Any?>) =
    ListenableWorker.Result.success(successWorkDataOf(pairs = pairs))

fun failureResult(message: String, vararg pairs: Pair<String, Any?>) =
    ListenableWorker.Result.success(failureWorkDataOf(message = message, pairs = pairs))
