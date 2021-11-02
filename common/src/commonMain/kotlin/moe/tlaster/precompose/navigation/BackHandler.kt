package moe.tlaster.precompose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import moe.tlaster.precompose.ui.BackHandler
import moe.tlaster.precompose.ui.LocalBackDispatcherOwner
import moe.tlaster.precompose.ui.LocalLifecycleOwner

@Composable
public fun BackHandler(onBack: () -> Unit) {
    // Safely update the current `onBack` lambda when a new one is provided
    val currentOnBack by rememberUpdatedState(onBack)
    // Remember in Composition a back callback that calls the `onBack` lambda
    val backCallback = remember {
        object : BackHandler {
            override fun handleBackPress(): Boolean {
                currentOnBack.invoke()
                return true
            }
        }
    }

    val backDispatcher = checkNotNull(LocalBackDispatcherOwner.current) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }.backDispatcher
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, backDispatcher) {
        // Add callback to the backDispatcher
        // backDispatcher.addCallback(lifecycleOwner, backCallback)
        backDispatcher.register(backCallback)
        // When the effect leaves the Composition, remove the callback
        onDispose {
            backDispatcher.unregister(backCallback)
        }
    }
}