package com.twidere.twiderex.viewmodel.twitter

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.twidere.twiderex.repository.AccountRepository

class ActiveAccountViewModel @ViewModelInject constructor(
    private val repository: AccountRepository,
) : ViewModel() {
    val account by lazy {
        repository.getCurrentAccount()
    }
}