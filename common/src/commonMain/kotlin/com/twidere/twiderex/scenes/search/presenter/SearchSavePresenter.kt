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
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull

@Composable
fun SearchSavePresenter(
  event: Flow<SearchSaveEvent>,
  repository: SearchRepository = get(),
  accountRepository: AccountRepository = get(),
  content: String,
): SearchSaveState {

  val account = accountRepository.activeAccount.mapNotNull { it }

  var loading by remember {
    mutableStateOf(false)
  }

  var isSaved by remember {
    mutableStateOf(false)
  }

  LaunchedEffect(Unit) {
    account.firstOrNull()?.let {
      isSaved = repository.get(content, it.accountKey)?.saved ?: false
    }
  }

  LaunchedEffect(Unit) {
    event.collect {
      when (it) {
        is SearchSaveEvent.Save -> {
          loading = true
          try {
            account.firstOrNull()?.let {
              repository.addOrUpgrade(
                content = content,
                accountKey = it.accountKey,
                saved = true
              )
              isSaved = true
            }
          } catch (e: Exception) {
            isSaved = false
          } finally {
            loading = false
          }
        }
      }
    }
  }

  return SearchSaveState(
    loading = loading,
    isSaved = isSaved
  )
}

interface SearchSaveEvent {
  object Save : SearchSaveEvent
}

data class SearchSaveState(
  val loading: Boolean,
  val isSaved: Boolean,
)
