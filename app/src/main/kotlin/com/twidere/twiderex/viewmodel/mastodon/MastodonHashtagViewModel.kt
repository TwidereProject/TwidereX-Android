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
package com.twidere.twiderex.viewmodel.mastodon

import com.twidere.services.mastodon.MastodonService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.paging.mediator.MastodonHashtagTimelineMediator
import com.twidere.twiderex.paging.mediator.paging.PagingMediator
import com.twidere.twiderex.viewmodel.PagingViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class MastodonHashtagViewModel @AssistedInject constructor(
    database: CacheDatabase,
    inAppNotification: InAppNotification,
    @Assisted keyword: String,
    @Assisted account: AccountDetails,
) : PagingViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            keyword: String,
            account: AccountDetails,
        ): MastodonHashtagViewModel
    }
    override val pagingMediator: PagingMediator = MastodonHashtagTimelineMediator(
        keyword = keyword,
        service = account.service as MastodonService,
        accountKey = account.accountKey,
        database = database,
        inAppNotification = inAppNotification
    )
}
