/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.viewmodel

import android.Manifest.permission
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.twidere.services.microblog.LookupService
import com.twidere.services.twitter.TwitterService
import com.twidere.twiderex.di.assisted.IAssistedFactory
import com.twidere.twiderex.extensions.getCachedLocation
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.repository.DraftRepository
import com.twidere.twiderex.repository.twitter.TwitterTweetsRepository
import com.twidere.twiderex.scenes.ComposeType
import com.twidere.twiderex.utils.ComposeQueue

class DraftItemViewModel @AssistedInject constructor(
    private val repository: DraftRepository,
    @Assisted private val draftId: String,
) : ViewModel() {

    val draft = liveData {
        repository.get(draftId)?.let {
            emit(it)
        }
    }

    @AssistedInject.Factory
    interface AssistedFactory : IAssistedFactory {
        fun create(
            draftId: String,
        ): DraftItemViewModel
    }
}

class DraftComposeViewModel @AssistedInject constructor(
    draftRepository: DraftRepository,
    locationManager: LocationManager,
    composeQueue: ComposeQueue,
    factory: TwitterTweetsRepository.AssistedFactory,
    @Assisted account: AccountDetails,
    @Assisted statusId: String?,
    @Assisted composeType: ComposeType,
    @Assisted initialText: String,
    @Assisted initialMedia: List<String>,
    @Assisted private val draftId: String,
) : ComposeViewModel(
    draftRepository,
    locationManager,
    composeQueue,
    factory,
    account,
    statusId,
    composeType
) {

    init {
        setText(initialText)
        putImages(initialMedia.map { Uri.parse(it) })
    }

    @AssistedInject.Factory
    interface AssistedFactory : IAssistedFactory {
        fun create(
            account: AccountDetails,
            statusId: String?,
            composeType: ComposeType,
            initialText: String,
            initialMedia: List<String>,
            draftId: String,
        ): DraftComposeViewModel
    }

    override fun saveDraft() {
        text.value?.let { text ->
            draftRepository.saveOrUpgrade(
                text,
                images.value?.map { it.toString() } ?: emptyList(),
                composeType,
                statusId,
                draftId = draftId,
            )
        }
    }

    override fun compose() {
        text.value?.let {
            composeQueue.commit(
                service,
                it,
                images = images.value ?: emptyList(),
                replyTo = if (composeType == ComposeType.Reply) status.value?.statusId else null,
                quoteTo = if (composeType == ComposeType.Quote) status.value?.statusId else null,
                draftId = draftId
            )
        }
    }
}

open class ComposeViewModel @AssistedInject constructor(
    protected val draftRepository: DraftRepository,
    protected val locationManager: LocationManager,
    protected val composeQueue: ComposeQueue,
    protected val factory: TwitterTweetsRepository.AssistedFactory,
    @Assisted protected val account: AccountDetails,
    @Assisted protected val statusId: String?,
    @Assisted val composeType: ComposeType,
) : ViewModel(), LocationListener {
    @AssistedInject.Factory
    interface AssistedFactory : IAssistedFactory {
        fun create(
            account: AccountDetails,
            statusId: String?,
            composeType: ComposeType
        ): ComposeViewModel
    }

    protected val service by lazy {
        account.service as TwitterService
    }
    protected val repository by lazy {
        factory.create(
            account.key,
            account.service as LookupService,
        )
    }
    val canSaveDraft = MutableLiveData(false)
    val text = MutableLiveData("")
    val images = MutableLiveData<List<Uri>>(emptyList())
    val location = MutableLiveData<Location>()
    val locationEnabled = MutableLiveData(false)
    val status = liveData {
        statusId?.let {
            emitSource(repository.loadTweetFromCache(it))
        } ?: run {
            emit(null)
        }
    }

    fun setText(value: String) {
        text.postValue(value)
        canSaveDraft.postValue(true)
    }

    open fun compose() {
        text.value?.let {
            composeQueue.commit(
                service,
                it,
                images = images.value ?: emptyList(),
                replyTo = if (composeType == ComposeType.Reply) status.value?.statusId else null,
                quoteTo = if (composeType == ComposeType.Quote) status.value?.statusId else null,
            )
        }
    }

    open fun saveDraft() {
        text.value?.let { text ->
            draftRepository.saveOrUpgrade(
                text,
                images.value?.map { it.toString() } ?: emptyList(),
                composeType,
                statusId,
            )
        }
    }

    fun putImages(value: List<Uri>) {
        images.value?.let {
            value + it
        }?.let {
            it.take(4)
        }?.let {
            images.postValue(it)
        }
        canSaveDraft.postValue(true)
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

    // compatibility fix for Api < 22
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onCleared() {
        if (locationEnabled.value == true) {
            locationManager.removeUpdates(this)
        }
    }

    fun removeImage(item: Uri) {
        images.value?.let {
            it - item
        }?.let {
            images.postValue(it)
        }
    }
}
