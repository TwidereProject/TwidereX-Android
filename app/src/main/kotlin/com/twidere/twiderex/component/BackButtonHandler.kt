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
package com.twidere.twiderex.component

import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

/**
 * Related discussion -
 * https://kotlinlang.slack.com/archives/CJLTWPH7S/p1591558155394500?thread_ts=1591558024.394400&cid=CJLTWPH7S
 */
private val LocalBackPressedDispatcher = staticCompositionLocalOf<OnBackPressedDispatcherOwner?> { null }

private class ComposableBackHandler(enabled: Boolean) : OnBackPressedCallback(enabled) {
    lateinit var onBackPressed: () -> Unit

    override fun handleOnBackPressed() {
        onBackPressed()
    }
}

@Composable
internal fun handler(
    enabled: Boolean = true,
    onBackPressed: () -> Unit
) {
    val dispatcher = (LocalBackPressedDispatcher.current ?: return).onBackPressedDispatcher
    val handler = remember { ComposableBackHandler(enabled) }
    DisposableEffect(dispatcher) {
        dispatcher.addCallback(handler)
        onDispose { handler.remove() }
    }
    DisposableEffect(enabled) {
        handler.isEnabled = enabled
        onDispose { }
    }
    DisposableEffect(onBackPressed) {
        handler.onBackPressed = onBackPressed
        onDispose { }
    }
}

@Composable
internal fun BackButtonHandler(
    onBackPressed: () -> Unit,
) {
    var context = LocalContext.current
    // Inspired from https://cs.android.com/androidx/platform/frameworks/support/+/
    // androidx-master-dev:navigation/navigation-compose/src/main/java/androidx/navigation/
    // compose/NavHost.kt;l=88
    // This was necessary because using Jetpack Navigation does not allow typecasting a
    // NavBackStackEntry to LifecycleOwnerLocal.
    while (context is ContextWrapper) {
        if (context is OnBackPressedDispatcherOwner) {
            break
        }
        context = context.baseContext
    }
    Providers(
        LocalBackPressedDispatcher provides context as ComponentActivity
    ) {
        handler {
            onBackPressed()
        }
    }
}
