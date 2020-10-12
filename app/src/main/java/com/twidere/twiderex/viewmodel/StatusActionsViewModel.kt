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

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.repository.StatusRepository
import kotlinx.coroutines.launch

class StatusActionsViewModel @ViewModelInject constructor(
    private val repository: StatusRepository
) : ViewModel() {
    fun like(status: UiStatus) = viewModelScope.launch {
        if (status.liked) {
            repository.unlike(status.statusId)
        } else {
            repository.like(status.statusId)
        }
    }

    fun retweet(status: UiStatus) = viewModelScope.launch {
        if (status.retweeted) {
            repository.unRetweet(status.statusId)
        } else {
            repository.retweet(status.statusId)
        }
    }
}
