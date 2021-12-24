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

import com.twidere.twiderex.kmp.ExifScrambler
import com.twidere.twiderex.kmp.FileResolver
import com.twidere.twiderex.kmp.LocationProvider
import com.twidere.twiderex.kmp.MediaInsertProvider
import com.twidere.twiderex.kmp.OrientationSensorManager
import com.twidere.twiderex.kmp.RemoteNavigator
import com.twidere.twiderex.kmp.ResLoader
import com.twidere.twiderex.notification.AppNotificationManager
import org.koin.dsl.module

actual val kmpModule = module {
    single { ExifScrambler(get()) }
    single { FileResolver(get()) }
    single { LocationProvider(get()) }
    single { RemoteNavigator(get()) }
    single { ResLoader(get()) }
    single { AppNotificationManager(get(), get()) }
    single { MediaInsertProvider(get()) }
    single { OrientationSensorManager(get()) }
}
