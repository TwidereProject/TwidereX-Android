package com.twidere.twiderex.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twidere.services.microblog.model.IRelationship
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.repository.UserRepository

class UserViewModel @ViewModelInject constructor(
    private val repository: UserRepository,
) : ViewModel() {
    val loaded = MutableLiveData(false)
    val user = MutableLiveData<UiUser>()
    val relationship = MutableLiveData<IRelationship>()


    suspend fun init(data: UiUser) {
        if (loaded.value == true) {
            return
        }
        user.postValue(data)
        repository.lookupUser(data.id)?.let {
            user.postValue(it)
        }
        repository.showRelationship(data.id)?.let {
            relationship.postValue(it)
        }
        loaded.postValue(true)
    }
}

