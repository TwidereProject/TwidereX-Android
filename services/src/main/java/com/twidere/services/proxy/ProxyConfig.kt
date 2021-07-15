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
package com.twidere.services.proxy

import okhttp3.Credentials
import okhttp3.OkHttpClient
import java.net.InetSocketAddress
import java.net.Proxy

data class ProxyConfig(
    val enable: Boolean = false,
    val server: String = "",
    val port: Int = 0,
    val userName: String = "",
    val password: String = "",
    val type: Type = Type.HTTP,
) {

    enum class Type {
        HTTP,
        REVERSE
    }

    fun generateProxyClientBuilder(): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
        return if (enable) {
            when (type) {
                Type.HTTP -> {
                    if (port !in (0..65535)) {
                        return builder
                    }
                    val address = InetSocketAddress.createUnresolved(
                        server,
                        port
                    )
                    builder.proxy(Proxy(Proxy.Type.HTTP, address))
                        .proxyAuthenticator { _, response ->
                            val b = response.request.newBuilder()
                            if (response.code == 407) {
                                if (userName.isNotEmpty() &&
                                    password.isNotEmpty()
                                ) {
                                    val credential = Credentials.basic(
                                        userName,
                                        password
                                    )
                                    b.header("Proxy-Authorization", credential)
                                }
                            }
                            b.build()
                        }
                }
                Type.REVERSE -> {
                    builder.addInterceptor(ReverseProxyInterceptor(server, userName, password))
                }
            }
        } else {
            builder
        }
    }

    /**
     * Intercept and replace proxy patterns to real URL
     */
}
