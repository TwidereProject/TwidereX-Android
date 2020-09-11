package com.twidere.twiderex.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.twidere.twiderex.repository.AccountRepository

class SplashViewModel @ViewModelInject constructor(
    private val repository: AccountRepository
) : ViewModel() {
    fun hasAccount() = repository.hasAccount()
}