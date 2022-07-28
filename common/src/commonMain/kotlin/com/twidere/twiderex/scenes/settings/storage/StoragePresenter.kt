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
package com.twidere.twiderex.scenes.settings.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.repository.CacheRepository
import kotlinx.coroutines.flow.Flow

@Composable
fun StoragePresenter(
  event: Flow<StorageEvent>,
  repository: CacheRepository = get(),
): StorageState {
  var loading by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    event.collect {
      when (it) {
        StorageEvent.ClearAllCaches -> {
          loading = true
          repository.clearDatabaseCache()
          repository.clearCacheDir()
          repository.clearImageCache()
          loading = false
        }
        StorageEvent.ClearImageCache -> {
          loading = true
          repository.clearImageCache()
          loading = false
        }
        StorageEvent.ClearSearchHistory -> {
          loading = true
          repository.clearSearchHistory()
          loading = false
        }
      }
    }
  }
  return StorageState(
    loading = loading
  )
}

data class StorageState(
  val loading: Boolean,
)

sealed interface StorageEvent {
  object ClearImageCache : StorageEvent
  object ClearSearchHistory : StorageEvent
  object ClearAllCaches : StorageEvent
}
