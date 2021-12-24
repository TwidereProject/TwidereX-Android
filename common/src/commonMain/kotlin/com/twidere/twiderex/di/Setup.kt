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
package com.twidere.twiderex.di

import com.twidere.twiderex.di.modules.actionModule
import com.twidere.twiderex.di.modules.dataBaseModule
import com.twidere.twiderex.di.modules.jobsModule
import com.twidere.twiderex.di.modules.kmpModule
import com.twidere.twiderex.di.modules.platformModule
import com.twidere.twiderex.di.modules.preferencesModule
import com.twidere.twiderex.di.modules.repositoryModule
import com.twidere.twiderex.di.modules.storageProviderModule
import com.twidere.twiderex.di.modules.viewModelModule
import com.twidere.twiderex.utils.OAuthLauncher
import org.koin.core.KoinApplication
import org.koin.dsl.module

fun KoinApplication.setupModules() {
    modules(storageProviderModule)
    modules(preferencesModule)
    modules(dataBaseModule)
    modules(platformModule)
    modules(viewModelModule)
    modules(repositoryModule)
    modules(actionModule)
    modules(jobsModule)
    modules(kmpModule)
    modules(
        module {
            single {
                OAuthLauncher(get())
            }
        }
    )
}
