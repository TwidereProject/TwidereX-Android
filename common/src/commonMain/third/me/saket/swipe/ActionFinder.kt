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
package me.saket.swipe

import kotlin.math.abs

internal data class SwipeActionMeta(
  val value: SwipeAction,
  val isOnRightSide: Boolean,
)

internal data class ActionFinder(
  private val left: List<SwipeAction>,
  private val right: List<SwipeAction>
) {

  fun actionAt(offset: Float, totalWidth: Int): SwipeActionMeta? {
    if (offset == 0f) {
      return null
    }

    val isOnRightSide = offset < 0f
    val actions = if (isOnRightSide) right else left

    val actionAtOffset = actions.actionAt(
      offset = abs(offset).coerceAtMost(totalWidth.toFloat()),
      totalWidth = totalWidth
    )
    return actionAtOffset?.let {
      SwipeActionMeta(
        value = actionAtOffset,
        isOnRightSide = isOnRightSide
      )
    }
  }

  private fun List<SwipeAction>.actionAt(offset: Float, totalWidth: Int): SwipeAction? {
    if (isEmpty()) {
      return null
    }

    val totalWeights = this.sumOf { it.weight }
    var offsetSoFar = 0.0

    @Suppress("ReplaceManualRangeWithIndicesCalls") // Avoid allocating an Iterator for every pixel swiped.
    for (i in 0 until size) {
      val action = this[i]
      val actionWidth = (action.weight / totalWeights) * totalWidth
      val actionEndX = offsetSoFar + actionWidth

      if (offset <= actionEndX) {
        return action
      }
      offsetSoFar += actionEndX
    }

    // Precision error in the above loop maybe?
    error("Couldn't find any swipe action. Width=$totalWidth, offset=$offset, actions=$this")
  }
}
