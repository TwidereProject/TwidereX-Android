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