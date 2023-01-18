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
package com.twidere.services.http

import com.twidere.services.proxy.ProxyConfig
import com.twidere.services.proxy.ReverseProxyInterceptor
import okhttp3.Credentials
import okhttp3.OkHttpClient
import java.net.InetSocketAddress
import java.net.Proxy

fun OkHttpClient.Builder.proxy(
  proxyConfig: ProxyConfig,
): OkHttpClient.Builder {
  if (!proxyConfig.enable) return this
  when (proxyConfig.type) {
    ProxyConfig.Type.HTTP -> proxy(
      proxyType = Proxy.Type.HTTP,
      host = proxyConfig.server,
      port = proxyConfig.port,
      username = proxyConfig.userName,
      password = proxyConfig.password,
    )
    ProxyConfig.Type.SOCKS -> proxy(
      proxyType = Proxy.Type.SOCKS,
      host = proxyConfig.server,
      port = proxyConfig.port,
      username = proxyConfig.userName,
      password = proxyConfig.password,
    )
    ProxyConfig.Type.REVERSE -> addInterceptor(
      ReverseProxyInterceptor(
        proxyConfig.server,
        proxyConfig.userName,
        proxyConfig.password,
      )
    )
  }
  return this
}

fun OkHttpClient.Builder.proxy(
  proxyType: Proxy.Type,
  host: String,
  port: Int,
  username: String,
  password: String,
) {
  if (port !in (0..65535)) return
  val address = InetSocketAddress.createUnresolved(host, port)
  proxy(Proxy(proxyType, address))
    .proxyAuthenticator { _, response ->
      val b = response.request.newBuilder()
      if (response.code == 407) {
        if (username.isNotEmpty() && password.isNotEmpty()) {
          val credential = Credentials.basic(username, password)
          b.header("Proxy-Authorization", credential)
        }
      }
      b.build()
    }
}
