package moe.tlaster.precompose.navigation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import moe.tlaster.precompose.navigation.transition.NavTransition

@Stable
internal class RouteStack(
    val id: Long,
    val scene: BackStackEntry,
    val dialogStack: SnapshotStateList<BackStackEntry> = mutableStateListOf(),
    val navTransition: NavTransition? = null,
) : LifecycleOwner {
    val currentEntry: BackStackEntry
        get() = if (dialogStack.any()) {
            dialogStack.last()
        } else {
            scene
        }
    private val lifecycleRegistry by lazy {
        LifecycleRegistry(this)
    }

    val canGoBack: Boolean
        get() = dialogStack.isNotEmpty()

    fun goBack(): BackStackEntry {
        return dialogStack.removeLast().apply {
            viewModelStore.clear()
        }
    }

    fun onActive() {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    fun onInActive() {
        lifecycleRegistry.currentState.let {
            if (it != Lifecycle.State.DESTROYED) {
                lifecycleRegistry.currentState = Lifecycle.State.STARTED
            }
        }
    }

    fun onDestroyed() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        dialogStack.forEach {
            it.viewModelStore.clear()
        }
        scene.viewModelStore.clear()
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}
