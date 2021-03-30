package moe.tlaster.precompose.navigation

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import moe.tlaster.precompose.navigation.transition.AnimatedRoute
import moe.tlaster.precompose.navigation.transition.NavTransition

/**
 * Provides in place in the Compose hierarchy for self contained navigation to occur.
 *
 * Once this is called, any Composable within the given [RouteBuilder] can be navigated to from
 * the provided [RouteBuilder].
 *
 * The builder passed into this method is [remember]ed. This means that for this NavHost, the
 * contents of the builder cannot be changed.
 *
 * @param navController the Navigator for this host
 * @param initialRoute the route for the start destination
 * @param navTransition navigation transition for the scenes in this [NavHost]
 * @param builder the builder used to construct the graph
 */
@Composable
fun NavHost(
    navController: NavController,
    initialRoute: String,
    navTransition: NavTransition = remember { NavTransition() },
    builder: RouteBuilder.() -> Unit,
) {
    val stateHolder = rememberSaveableStateHolder()
    val manager = remember {
        val graph = RouteBuilder(initialRoute = initialRoute).apply(builder).build()
        RouteStackManager(stateHolder, graph).apply {
            navController.stackManager = this
        }
    }

    val lifecycleOwner = checkNotNull(LocalLifecycleOwner.current) {
        "NavHost requires a LifecycleOwner to be provided via LocalLifecycleOwner"
    }
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "NavHost requires a ViewModelStoreOwner to be provided via LocalViewModelStoreOwner"
    }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current.onBackPressedDispatcher
    DisposableEffect(manager, lifecycleOwner, viewModelStoreOwner, backDispatcher) {
        manager.lifeCycleOwner = lifecycleOwner
        manager.setViewModelStore(viewModelStoreOwner.viewModelStore)
        manager.backDispatcher = backDispatcher
        onDispose {
            manager.lifeCycleOwner = null
        }
    }

    LaunchedEffect(manager, initialRoute) {
        manager.navigate(initialRoute)
    }
    val currentStack = manager.currentStack
    if (currentStack != null) {
        AnimatedRoute(
            currentStack,
            navTransition = navTransition,
            manager = manager,
        ) { routeStack ->
            LaunchedEffect(routeStack) {
                routeStack.onActive()
            }
            DisposableEffect(routeStack) {
                onDispose {
                    routeStack.onInActive()
                }
            }
            CompositionLocalProvider(
                LocalLifecycleOwner provides routeStack,
            ) {
                stateHolder.SaveableStateProvider(routeStack.id) {
                    CompositionLocalProvider(
                        LocalViewModelStoreOwner provides routeStack.scene
                    ) {
                        routeStack.scene.route.content.invoke(routeStack.scene)
                    }
                    routeStack.dialogStack.forEach { backStackEntry ->
                        CompositionLocalProvider(
                            LocalViewModelStoreOwner provides backStackEntry
                        ) {
                            Box(
                                modifier = Modifier
                                    .pointerInput(Unit) {
                                        forEachGesture {
                                            awaitPointerEventScope {
                                                awaitPointerEvent().changes.forEach { it.consumeAllChanges() }
                                            }
                                        }
                                    }
                            ) {
                                backStackEntry.route.content.invoke(backStackEntry)
                            }
                        }
                    }
                }
            }
        }
    }
}
