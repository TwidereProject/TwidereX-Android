package com.twidere.twiderex.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import com.twidere.twiderex.repository.AccountRepository

class MeViewModel @ViewModelInject constructor(
    private val repository: AccountRepository,
): ViewModel() {
    val user = repository.getCurrentAccount().user.toUi()
}