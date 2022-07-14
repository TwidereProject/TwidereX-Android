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

import com.twidere.twiderex.dataprovider.DataProvider
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.http.TwidereHttpConfigProvider
import com.twidere.twiderex.model.AccountPreferencesFactory
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.utils.PlatformResolver
import org.koin.dsl.module

internal actual val platformModule = module {
    single { AccountPreferencesFactory() }
    single { AccountRepository(get<DataProvider>().realAppDatabase.accountQueries, get()) }
    single { InAppNotification() }
    single { TwidereHttpConfigProvider(get<PreferencesHolder>().miscPreferences) }
    single { PlatformResolver(get()) }
}
