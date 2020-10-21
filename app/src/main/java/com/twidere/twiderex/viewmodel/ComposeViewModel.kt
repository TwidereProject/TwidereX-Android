/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.viewmodel

import android.Manifest.permission
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twidere.services.microblog.StatusService
import com.twidere.twiderex.extensions.getCachedLocation
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.utils.ComposeQueue

class ComposeViewModel @ViewModelInject constructor(
    private val repository: AccountRepository,
    private val locationManager: LocationManager,
) : ViewModel(), LocationListener {
    private val service by lazy {
        repository.getCurrentAccount().service as StatusService
    }
    val images = MutableLiveData<List<Uri>>(emptyList())
    val location = MutableLiveData<Location>()
    val locationEnabled = MutableLiveData(false)

    fun compose(content: String) {
        ComposeQueue.commit(service, content)
    }

    fun putImages(value: List<Uri>) {
        images.value?.let {
            value + it
        }?.let {
            it.take(4)
        }?.let {
            images.postValue(it)
        }
    }

    @RequiresPermission(anyOf = [permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION])
    fun trackingLocation() {
        locationEnabled.postValue(true)
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider = locationManager.getBestProvider(criteria, true) ?: return
        locationManager.requestLocationUpdates(provider, 0, 0f, this)
        locationManager.getCachedLocation()?.let {
            location.postValue(it)
        }
    }

    fun disableLocation() {
        locationEnabled.postValue(false)
        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        this.location.postValue(location)
    }

    override fun onCleared() {
        if (locationEnabled.value == true) {
            locationManager.removeUpdates(this)
        }
    }
}
