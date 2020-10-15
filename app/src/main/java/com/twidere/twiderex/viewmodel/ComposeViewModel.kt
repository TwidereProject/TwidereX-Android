package com.twidere.twiderex.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.twidere.services.microblog.StatusService
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.utils.ComposeQueue

class ComposeViewModel @ViewModelInject constructor(
    private val repository: AccountRepository,
) : ViewModel() {
    private val service by lazy {
        repository.getCurrentAccount().service as StatusService
    }

    fun compose(content: String) {
        ComposeQueue.commit(service, content)
    }
}