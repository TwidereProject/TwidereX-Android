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
package com.twidere.twiderex.utils

import com.twidere.twiderex.BuildConfig
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TwiderexLogger : Antilog() {

  private val logger by lazy { DebugAntilog() }
  private val scope by lazy {
    CoroutineScope(Dispatchers.IO)
  }

  override fun isEnable(priority: LogLevel, tag: String?): Boolean = BuildConfig.Debug

  override fun performLog(priority: LogLevel, tag: String?, throwable: Throwable?, message: String?) {
    scope.launch {
      logger.log(priority, tag, throwable, message)
    }
  }
}
