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
package moe.tlaster.precompose.navigation.transition

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import moe.tlaster.precompose.lifecycle.Lifecycle
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.RouteStack

@Composable
internal fun AnimatedDialogRoute(
    stack: RouteStack,
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    dialogTransition: DialogTransition = remember { DialogTransition() },
    content: @Composable (BackStackEntry) -> Unit
) {

    val items = remember { mutableStateListOf<AnimatedRouteItem<BackStackEntry>>() }
    val stacks = stack.stacks
    val targetState = remember(stack.stacks.size) {
        stack.currentEntry
    }
    val transitionState = remember { MutableTransitionState(targetState) }
    val targetChanged = (targetState != transitionState.targetState)
    val previousState = transitionState.targetState
    transitionState.targetState = targetState
    val transition = updateTransition(transitionState, label = "AnimatedDialogRouteTransition")

    if (targetChanged || items.isEmpty()) {
        val indexOfNew = stacks.indexOf(targetState).takeIf { it >= 0 } ?: Int.MAX_VALUE
        val indexOfOld = stacks.indexOf(previousState)
            .takeIf {
                it >= 0 ||
                    // Workaround for navOptions
                    targetState?.lifecycle?.currentState == Lifecycle.State.Initialized &&
                    previousState?.lifecycle?.currentState == Lifecycle.State.Active
            } ?: Int.MAX_VALUE
        // Only manipulate the list when the state is changed, or in the first run.
        val keys = items.map {
            val type = if (indexOfNew >= indexOfOld) AnimateType.Pause else AnimateType.Destroy
            it.key to type
        }.toMap().run {
            toMutableMap().also {
                val type = if (indexOfNew >= indexOfOld) {
                    AnimateType.Create
                } else {
                    AnimateType.Resume
                }
                if (targetState != null) {
                    it[targetState] = type
                }
            }
        }
        items.clear()
        keys.mapTo(items) { (key, value) ->
            AnimatedRouteItem(key, value) {
                val factor by transition.animateFloat(
                    transitionSpec = { animationSpec }
                ) { if (it == key) 1f else 0f }
                Box(
                    Modifier.graphicsLayer {
                        when (value) {
                            AnimateType.Create -> dialogTransition.createTransition.invoke(
                                this,
                                factor
                            )
                            AnimateType.Destroy -> dialogTransition.destroyTransition.invoke(
                                this,
                                factor
                            )
                            else -> Unit
                        }
                    }
                ) {
                    content(key)
                }
            }
        }.sortByDescending { it.animateType }
    } else if (transitionState.currentState == transitionState.targetState) {
        // Remove all the intermediate items from the list once the animation is finished.
        items.removeAll { it.animateType == AnimateType.Destroy }
    }

    Box(modifier) {
        for (index in items.indices) {
            val item = items[index]
            key(item.key) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    item.content.invoke()
                }
            }
        }
    }
}
