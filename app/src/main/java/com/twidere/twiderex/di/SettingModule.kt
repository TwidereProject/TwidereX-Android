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
package com.twidere.twiderex.di

import android.content.SharedPreferences
import com.twidere.twiderex.settings.PrimaryColorSetting
import com.twidere.twiderex.settings.TabPositionSetting
import com.twidere.twiderex.settings.ThemeSetting
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object SettingModule {

    @Provides
    @Singleton
    fun provideTapPosition(preferences: SharedPreferences): TabPositionSetting =
        TabPositionSetting(preferences)

    @Provides
    @Singleton
    fun provideTheme(preferences: SharedPreferences): ThemeSetting =
        ThemeSetting(preferences)

    @Provides
    @Singleton
    fun providePrimaryColor(preferences: SharedPreferences): PrimaryColorSetting =
        PrimaryColorSetting(preferences)
}
