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
package com.twidere.services

import com.twidere.services.http.HttpConfigProvider
import com.twidere.services.http.authorization.EmptyAuthorization
import com.twidere.services.http.config.HttpConfig
import com.twidere.services.http.config.HttpConfigClientFactory
import com.twidere.services.twitter.api.TwitterResources
import org.junit.jupiter.api.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class HttpConfigClientFactoryTest {
    private var config = HttpConfig()
    private val configProvider = object : HttpConfigProvider {
        override fun provideConfig(): HttpConfig {
            return config
        }
    }

    @Test
    fun createResourceUsingCache() {
        val factory = HttpConfigClientFactory(configProvider)
        val resourceOld = factory.createResources<TwitterResources>(
            TwitterResources::class.java,
            baseUrl = "https://www.twitter.com",
            EmptyAuthorization(),
            useCache = true
        )

        val resourceNew = factory.createResources<TwitterResources>(
            TwitterResources::class.java,
            baseUrl = "https://www.twitter.com",
            EmptyAuthorization(),
            useCache = true
        )
        assertSame(resourceOld, resourceNew)
    }

    @Test
    fun createResourceNotUsingCache() {
        val factory = HttpConfigClientFactory(configProvider)
        val resourceOld = factory.createResources<TwitterResources>(
            TwitterResources::class.java,
            baseUrl = "https://www.twitter.com",
            EmptyAuthorization(),
        )

        val resourceNew = factory.createResources<TwitterResources>(
            TwitterResources::class.java,
            baseUrl = "https://www.twitter.com",
            EmptyAuthorization(),
        )
        assertNotSame(resourceOld, resourceNew)
    }
}
