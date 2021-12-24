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
package com.twidere.twiderex

import android.content.Context
import androidx.startup.AppInitializer
import com.twidere.twiderex.initializer.DirectMessageInitializer
import com.twidere.twiderex.initializer.NotificationChannelInitializer
import com.twidere.twiderex.initializer.NotificationInitializer
import com.twidere.twiderex.initializer.TwidereServiceInitializer

class TwidereApp : TwidereApplication() {
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
                initializeComponent(NotificationChannelInitializer::class.java)
                initializeComponent(NotificationInitializer::class.java)
                initializeComponent(DirectMessageInitializer::class.java)
                initializeComponent(TwidereServiceInitializer::class.java)
            }
    }

    interface MissingSplitsChecker {
        fun requiredSplits(context: Context): Boolean
    }
}
