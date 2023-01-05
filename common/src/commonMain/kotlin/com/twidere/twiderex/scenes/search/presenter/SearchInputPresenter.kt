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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.model.ui.UiSearch
import com.twidere.twiderex.repository.SearchRepository
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import kotlinx.coroutines.flow.Flow

private const val searchCount = 3

@Composable
fun SearchInputPresenter(
  events: Flow<SearchInputEvent>,
  repository: SearchRepository = get(),
  keyword: String
): SearchInputState {
  val accountState = CurrentAccountPresenter()

  if (accountState !is CurrentAccountState.Account) {
    return SearchInputState.NoAccount
  }

  var expandSearch by remember {
    mutableStateOf(false)
  }

  val source by remember(accountState) {
    repository.searchHistory(accountState.account.accountKey)
  }.collectAsState(emptyList())

  val savedSource by remember(accountState, expandSearch) {
    repository.savedSearch(accountState.account.accountKey)
  }.collectAsState(emptyList())

  val filteredSavedSource = remember(savedSource, expandSearch, searchCount) {
    savedSource.filterIndexed { index, _ ->
      index < searchCount || expandSearch
    }
  }

  var searchInput by remember {
    mutableStateOf(TextFieldValue(keyword, TextRange(keyword.length)))
  }

  val showExpand = remember(savedSource, searchCount) {
    savedSource.size > searchCount
  }

  LaunchedEffect(Unit) {
    events.collect {
      when (it) {
        is SearchInputEvent.RemoveEvent -> {
          repository.remove(it.item)
        }
        is SearchInputEvent.ChangeExpand -> {
          expandSearch = it.expandSearch
        }
        is SearchInputEvent.UpdateSearchInput -> {
          searchInput = it.searchInput
        }
        is SearchInputEvent.AddOrUpgradeEvent -> {
          accountState.account.let { account ->
            repository.addOrUpgrade(it.content, account.accountKey)
          }
        }
      }
    }
  }

  return SearchInputState.Data(
    expandSearch = expandSearch,
    searchInput = searchInput,
    source = source,
    savedSource = filteredSavedSource,
    showExpand = showExpand
  )
}

interface SearchInputState {
  data class Data(
    val expandSearch: Boolean,
    val searchInput: TextFieldValue,
    val source: List<UiSearch>,
    val savedSource: List<UiSearch>,
    val showExpand: Boolean
  ) : SearchInputState

  object NoAccount : SearchInputState
}

interface SearchInputEvent {
  data class RemoveEvent(val item: UiSearch) : SearchInputEvent
  data class AddOrUpgradeEvent(val content: String) : SearchInputEvent
  data class UpdateSearchInput(val searchInput: TextFieldValue) : SearchInputEvent
  data class ChangeExpand(val expandSearch: Boolean) : SearchInputEvent
}
