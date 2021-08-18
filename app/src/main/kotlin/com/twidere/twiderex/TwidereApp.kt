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
package com.twidere.twiderex

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.AppInitializer
import androidx.work.Configuration
import com.twidere.twiderex.http.TwidereServiceInitializer
import com.twidere.twiderex.notification.NotificationInitializer
import com.twidere.twiderex.worker.dm.DirectMessageInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TwidereApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Note:Installs with missing splits are now blocked on devices which have Play Protect active or run on Android 10.
        // But there are still some custom roms allows missing splits which causes resources not found exception
        if (MissingSplitsCheckerImpl().requiredSplits(this)) {
            return
        }
        // manually setup NotificationInitializer since it require HiltWorkerFactory
        AppInitializer.getInstance(this)
            .apply {
                initializeComponent(NotificationInitializer::class.java)
                initializeComponent(DirectMessageInitializer::class.java)
                initializeComponent(TwidereServiceInitializer::class.java)
            }
    }

    interface MissingSplitsChecker {
        fun requiredSplits(context: Context): Boolean
    }
}
