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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.model.ui.UiSearch
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull

@Composable
fun SearchInputPresenter(
  events: Flow<SearchInputEvent>,
  repository: SearchRepository = get(),
  accountRepository: AccountRepository = get(),
  keyword: String
): SearchInputState {

  val account = accountRepository.activeAccount.mapNotNull { it }

  @OptIn(ExperimentalCoroutinesApi::class)
  val source by account.flatMapLatest {
    repository.searchHistory(it.accountKey)
  }.collectAsState(emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val savedSource by account.flatMapLatest {
    repository.savedSearch(it.accountKey)
  }.collectAsState(emptyList())

  var searchInput by remember {
    mutableStateOf(TextFieldValue(keyword, TextRange(keyword.length)))
  }

  var expandSearch by remember {
    mutableStateOf(false)
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
          account.firstOrNull()?.let { account ->
            repository.addOrUpgrade(it.content, account.accountKey)
          }
        }
      }
    }
  }

  return SearchInputState(
    expandSearch = expandSearch,
    searchInput = searchInput,
    source = source,
    savedSource = savedSource
  )
}

data class SearchInputState(
  val expandSearch: Boolean,
  val searchInput: TextFieldValue,
  val source: List<UiSearch>,
  val savedSource: List<UiSearch>
)

interface SearchInputEvent {
  data class RemoveEvent(val item: UiSearch) : SearchInputEvent
  data class AddOrUpgradeEvent(val content: String) : SearchInputEvent
  data class UpdateSearchInput(val searchInput: TextFieldValue) : SearchInputEvent
  data class ChangeExpand(val expandSearch: Boolean) : SearchInputEvent
}
