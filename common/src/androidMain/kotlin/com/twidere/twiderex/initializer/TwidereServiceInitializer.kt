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
package com.twidere.twiderex.initializer

import android.content.Context
import androidx.startup.Initializer
import com.twidere.twiderex.http.TwidereHttpConfigProvider
import com.twidere.twiderex.http.TwidereServiceFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TwidereserviceInitializerHolder

class TwidereServiceInitializer : Initializer<TwidereserviceInitializerHolder>, KoinComponent {
    private val configProvider: TwidereHttpConfigProvider by inject()

    override fun create(context: Context): TwidereserviceInitializerHolder {
        TwidereServiceFactory.initiate(configProvider)
        return TwidereserviceInitializerHolder()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}
