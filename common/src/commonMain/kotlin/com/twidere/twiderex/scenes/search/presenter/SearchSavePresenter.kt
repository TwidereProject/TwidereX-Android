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
package com.twidere.twiderex.scenes.search.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.repository.SearchRepository
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import kotlinx.coroutines.flow.Flow

@Composable
fun SearchSavePresenter(
  event: Flow<SearchSaveEvent>,
  content: String,
  repository: SearchRepository = get(),
): SearchSaveState {
  val accountState = CurrentAccountPresenter()

  if (accountState !is CurrentAccountState.Account) {
    return SearchSaveState.NoAccount
  }

  var loading by remember {
    mutableStateOf(false)
  }

  var isSaved by remember {
    mutableStateOf(false)
  }

  LaunchedEffect(Unit) {
    isSaved = repository.get(content, accountState.account.accountKey)?.saved ?: false
  }

  LaunchedEffect(Unit) {
    event.collect {
      when (it) {
        is SearchSaveEvent.Save -> {
          loading = true
          isSaved = try {
            repository.addOrUpgrade(
              content = content,
              accountKey = accountState.account.accountKey,
              saved = true
            )
            true
          } catch (e: Exception) {
            false
          } finally {
            loading = false
          }
        }
      }
    }
  }

  return SearchSaveState.Data(
    loading = loading,
    isSaved = isSaved
  )
}

interface SearchSaveEvent {
  object Save : SearchSaveEvent
}

interface SearchSaveState {
  data class Data(
    val loading: Boolean,
    val isSaved: Boolean,
  ) : SearchSaveState
  object NoAccount : SearchSaveState
}
