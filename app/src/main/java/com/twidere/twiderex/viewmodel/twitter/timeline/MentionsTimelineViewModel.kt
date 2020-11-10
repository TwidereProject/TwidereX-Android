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
package com.twidere.twiderex.viewmodel.twitter.timeline

import android.content.SharedPreferences
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.di.assisted.IAssistedFactory
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.repository.timeline.MentionsTimelineRepository
import com.twidere.twiderex.repository.timeline.TimelineRepository

class MentionsTimelineViewModel @AssistedInject constructor(
    preferences: SharedPreferences,
    factory: MentionsTimelineRepository.AssistedFactory,
    @Assisted private val account: AccountDetails
) : TimelineViewModel(preferences) {
    @AssistedInject.Factory
    interface AssistedFactory: IAssistedFactory {
        fun create(account: AccountDetails): MentionsTimelineViewModel
    }
    override val repository: TimelineRepository by lazy {
        account.service.let {
            it as TimelineService
        }.let { service ->
            factory.create(account.key, service)
        }
    }
    override val savedStateKey: String = "${account.key}_mentions"
}
