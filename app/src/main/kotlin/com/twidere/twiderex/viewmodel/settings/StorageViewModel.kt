package com.twidere.twiderex.viewmodel.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twidere.twiderex.repository.CacheRepository
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class StorageViewModel @AssistedInject constructor(
    private val repository: CacheRepository,
): ViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): StorageViewModel
    }

    val loading = MutableLiveData(false)

    fun clearDatabaseCache() = viewModelScope.launch {
        loading.postValue(true)
        repository.clearDatabaseCache()
        loading.postValue(false)
    }

    fun clearImageCache() = viewModelScope.launch {
        loading.postValue(true)
        repository.clearImageCache()
        repository.clearCacheDir()
        loading.postValue(false)
    }

    fun clearSearchHistory() = viewModelScope.launch {
        loading.postValue(true)
        repository.clearSearchHistory()
        loading.postValue(false)
    }

}