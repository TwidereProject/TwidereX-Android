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
package app.cash.molecule

enum class RecompositionClock {
  /**
   * Use the MonotonicFrameClock that already exists in the calling CoroutineContext.
   * If none exists, an exception is thrown.
   *
   * Use this option to drive Molecule with the built-in Android frame clock.
   */
  ContextClock,

  /**
   * Install an eagerly recomposing clock. This clock will provide a new frame immediately whenever
   * one is requested. The resulting flow will emit a new item every time the snapshot state is invalidated.
   */
  Immediate,
}
