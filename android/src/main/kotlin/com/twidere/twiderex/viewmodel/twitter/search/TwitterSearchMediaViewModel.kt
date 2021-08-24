/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.viewmodel.twitter.search

import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.flatMap
import androidx.paging.map
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.transform.toUi
import com.twidere.twiderex.paging.mediator.paging.pager
import com.twidere.twiderex.paging.mediator.search.SearchMediaMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map

class TwitterSearchMediaViewModel @AssistedInject constructor(
    val database: CacheDatabase,
    @Assisted private val account: AccountDetails,
    @Assisted keyword: String,
) : ViewModel() {
    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(account: AccountDetails, keyword: String): TwitterSearchMediaViewModel
    }

    private val service by lazy {
        account.service as TwitterService
    }
    val source by lazy {
        SearchMediaMediator(keyword, database, account.accountKey, service).pager()
            .flow.map { it.map { it.status.toUi(account.accountKey) } }.cachedIn(viewModelScope)
            .map {
                it.flatMap {
                    it.media.map { media -> media to it }
                }
            }
    }
}
