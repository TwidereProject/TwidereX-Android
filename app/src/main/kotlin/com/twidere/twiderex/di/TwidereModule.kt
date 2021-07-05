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
import androidx.work.WorkManager
import com.twidere.services.nitter.NitterService
import com.twidere.twiderex.action.ComposeAction
import com.twidere.twiderex.action.DirectMessageAction
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.model.AccountPreferences
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.preferences.proto.MiscPreferences
import com.twidere.twiderex.utils.PlatformResolver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TwidereModule {
    @Singleton
    @Provides
    fun provideComposeQueue(
        workManager: WorkManager,
    ): ComposeAction = ComposeAction(workManager = workManager)

    @Singleton
    @Provides
    fun provideInAppNotification(): InAppNotification = InAppNotification()

    @Singleton
    @Provides
    fun providePlatformResolver(database: CacheDatabase): PlatformResolver =
        PlatformResolver(database = database)

    @Provides
    fun provideNitterService(preferences: DataStore<MiscPreferences>): NitterService? {
        return runBlocking {
            preferences.data.first().nitterInstance.takeIf { it.isNotEmpty() }
                ?.let { NitterService(it.trimEnd('/')) }
        }
    }

    @Singleton
    @Provides
    fun provideDirectMessageQueue(
        workManager: WorkManager,
    ): DirectMessageAction = DirectMessageAction(workManager = workManager)

    @Provides
    fun provideAccountPreferencesFactory(@ApplicationContext context: Context): AccountPreferences.Factory =
        AccountPreferences.Factory(context)
}
