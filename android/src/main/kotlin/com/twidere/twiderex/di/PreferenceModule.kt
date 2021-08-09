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
package com.twidere.twiderex.di

import androidx.datastore.core.DataStore
import com.twidere.twiderex.preferences.PreferencesHolder
import com.twidere.twiderex.preferences.model.AppearancePreferences
import com.twidere.twiderex.preferences.model.DisplayPreferences
import com.twidere.twiderex.preferences.model.MiscPreferences
import com.twidere.twiderex.preferences.model.NotificationPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.core.Koin
import javax.inject.Singleton

@Deprecated("Use koin directly")
@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {
    @Singleton
    @Provides
    fun provideAppearances(koin: Koin): DataStore<AppearancePreferences> =
        koin.get<PreferencesHolder>().appearancePreferences

    @Singleton
    @Provides
    fun provideDisplay(koin: Koin): DataStore<DisplayPreferences> =
        koin.get<PreferencesHolder>().displayPreferences

    @Singleton
    @Provides
    fun provideMisc(koin: Koin): DataStore<MiscPreferences> =
        koin.get<PreferencesHolder>().miscPreferences

    @Singleton
    @Provides
    fun provideNotification(koin: Koin): DataStore<NotificationPreferences> =
        koin.get<PreferencesHolder>().notificationPreferences
}
