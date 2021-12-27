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
package com.twidere.services.api

import com.twidere.services.api.common.MockApiAsset
import com.twidere.services.api.common.Request2AssetPathConvertor
import com.twidere.services.api.common.mockRetrofit
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import okhttp3.Request
import org.junit.jupiter.api.Test
import retrofit2.http.GET
import kotlin.test.assertEquals

@Serializable
data class AssetTest(
    val name: String
)

interface MockApiService {
    @GET("/asset_test")
    suspend fun fetchAssetTest(): AssetTest
}

class MockApiTest {
    @Test
    fun testFetchAsset() {
        val content = MockApiAsset.fetchAsset("/asset_test")
        assertEquals("{\"name\":\"test\"}", content)
    }

    @Test
    fun testRetrofit() {
        val service = mockRetrofit<MockApiService>(
            "https://www.test.com",
            object :
                Request2AssetPathConvertor {
                override fun url2Path(request: Request): String {
                    return request.url.encodedPath
                }
            }
        )
        runBlocking {
            val result = service.fetchAssetTest()
            assertEquals("test", result.name)
        }
    }
}
