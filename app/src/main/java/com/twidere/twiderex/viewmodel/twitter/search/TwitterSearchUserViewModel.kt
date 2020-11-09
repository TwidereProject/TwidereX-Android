/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.viewmodel.twitter.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.twidere.services.microblog.SearchService
import com.twidere.twiderex.defaultLoadCount
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.twitter.TwitterSearchUserRepository

class TwitterSearchUserViewModel @ViewModelInject constructor(
    private val accountRepository: AccountRepository,
    private val factory: TwitterSearchUserRepository.AssistedFactory,
) : TwitterSearchListViewModelBase() {
    private var page = 0
    private val repository by lazy {
        accountRepository.getCurrentAccount().service.let {
            it as SearchService
        }.let {
            factory.create(it, defaultLoadCount)
        }
    }

    val source = MutableLiveData<List<UiUser>>()

    override fun reset(keyword: String) {
        super.reset(keyword)
        source.postValue(emptyList())
        page = 0
    }

    override suspend fun refresh() {
        if (refreshing.value == true || loaded.value == true) {
            return
        }
        refreshing.postValue(true)
        reset(keyword)
        loadData()
        loaded.postValue(true)
        refreshing.postValue(false)
    }

    override suspend fun loadMore() {
        if (!hasMore || loadingMore.value == true) {
            return
        }
        loadingMore.postValue(true)
        loadData()
        loadingMore.postValue(false)
    }

    private suspend fun loadData() {
        val result = repository.loadUsers(keyword, page = page++)
        source.postValue((source.value ?: emptyList()) + result)
        hasMore = result.any() && result.count() > defaultLoadCount
    }
}
