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
package com.twidere.twiderex.viewmodel.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twidere.twiderex.repository.SearchRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class SearchSaveViewModel @AssistedInject constructor(
    private val repository: SearchRepository,
    @Assisted private val content: String,
) : ViewModel() {

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(content: String): SearchSaveViewModel
    }

    val loading = MutableLiveData(false)

    val isSaved = MutableLiveData(false)

    init {
        viewModelScope.launch {
            isSaved.postValue(repository.get(content)?.saved ?: false)
        }
    }

    fun save() {
        viewModelScope.launch {
            loading.postValue(true)
            try {
                repository.addOrUpgrade(content = content, saved = true)
                isSaved.postValue(true)
            } catch (e: Exception) {
                isSaved.postValue(false)
            } finally {
                loading.postValue(false)
            }
        }
    }
}
