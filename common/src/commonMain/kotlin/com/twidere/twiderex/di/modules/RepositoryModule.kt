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
package com.twidere.twiderex.di.modules

import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.repository.CacheRepository
import com.twidere.twiderex.repository.DirectMessageRepository
import com.twidere.twiderex.repository.DraftRepository
import com.twidere.twiderex.repository.GifRepository
import com.twidere.twiderex.repository.ListsRepository
import com.twidere.twiderex.repository.ListsUsersRepository
import com.twidere.twiderex.repository.MediaRepository
import com.twidere.twiderex.repository.NitterRepository
import com.twidere.twiderex.repository.NotificationRepository
import com.twidere.twiderex.repository.ReactionRepository
import com.twidere.twiderex.repository.SearchRepository
import com.twidere.twiderex.repository.StatusRepository
import com.twidere.twiderex.repository.TimelineRepository
import com.twidere.twiderex.repository.TrendRepository
import com.twidere.twiderex.repository.UserListRepository
import com.twidere.twiderex.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { CacheRepository(get(), get(), get()) }
    single { DirectMessageRepository(get()) }
    single { DraftRepository(get()) }
    single { ListsRepository(get()) }
    single { ListsUsersRepository() }
    single { MediaRepository(get()) }
    single { NotificationRepository(get()) }
    single { ReactionRepository(get()) }
    single { SearchRepository(get(), get()) }
    single { StatusRepository(get(), get<PreferencesHolder>().miscPreferences) }
    single { TimelineRepository(get()) }
    single { TrendRepository(get()) }
    single { UserListRepository() }
    single { UserRepository(get(), get()) }
    single { GifRepository(get()) }
    single { NitterRepository() }
}
