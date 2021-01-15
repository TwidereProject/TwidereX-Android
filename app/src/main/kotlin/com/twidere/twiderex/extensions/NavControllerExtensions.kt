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
package com.twidere.twiderex.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.onDispose
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun <T> NavController.navigateForResult(key: String, navAction: NavController.() -> Unit) = suspendCoroutine<T?> { continuation ->
    currentBackStackEntry?.savedStateHandle?.remove<T>(key) // clear latest value in mRegular
    currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)?.let { liveData ->
        val observer = object : Observer<T?> {
            override fun onChanged(result: T?) {
                continuation.resume(result)
                currentBackStackEntry?.savedStateHandle?.remove<T?>(key)
                liveData.removeObserver(this)
            }
        }
        liveData.observeForever(observer)
    }
    navAction.invoke(this)
}

@Composable
fun NavController.DisposeResult(key: String) {
    onDispose {
        currentBackStackEntry?.savedStateHandle?.set(key, null)
    }
}

inline fun <reified T> NavController.setResult(key: String, value: T) {
    previousBackStackEntry?.savedStateHandle?.set(
        key,
        value,
    )
}
