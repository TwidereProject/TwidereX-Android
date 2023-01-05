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
package com.twidere.twiderex.utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.net.ConnectivityManagerCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun ConnectivityManager.asIsActiveNetworkFlow(): Flow<Boolean> = callbackFlow {
  val request = NetworkRequest.Builder().build()
  val networkCallback = object : ConnectivityManager.NetworkCallback() {
    override fun onCapabilitiesChanged(
      network: Network,
      networkCapabilities: NetworkCapabilities,
    ) {
      trySend(ConnectivityManagerCompat.isActiveNetworkMetered(this@asIsActiveNetworkFlow))
    }
  }
  registerNetworkCallback(request, networkCallback)
  awaitClose { unregisterNetworkCallback(networkCallback) }
}
