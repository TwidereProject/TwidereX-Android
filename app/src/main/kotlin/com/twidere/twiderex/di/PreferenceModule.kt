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

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import com.twidere.twiderex.preferences.proto.AppearancePreferences
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.preferences.serializer.AppearancePreferencesSerializer
import com.twidere.twiderex.preferences.serializer.DisplayPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {
    @Singleton
    @Provides
    fun provideAppearances(@ApplicationContext context: Context): DataStore<AppearancePreferences> =
        context.createDataStore("appearances.pb", AppearancePreferencesSerializer)
    @Singleton
    @Provides
    fun provideDisplay(@ApplicationContext context: Context): DataStore<DisplayPreferences> =
        context.createDataStore("display.pb", DisplayPreferencesSerializer)
}

inline fun <reified T> Context.createDataStore(
    name: String,
    serializer: Serializer<T>,
) = DataStoreFactory.create(
    serializer,
    produceFile = {
        File(
            applicationContext.filesDir,
            "datastore/$name"
        )
    },
)
