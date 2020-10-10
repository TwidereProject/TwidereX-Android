package com.twidere.twiderex.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twidere.services.microblog.model.IRelationship
import com.twidere.twiderex.model.UserKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.UserRepository

class UserViewModel @ViewModelInject constructor(
    private val accountRepository: AccountRepository,
    private val repository: UserRepository,
) : ViewModel() {
    val loaded = MutableLiveData(false)
    val user = MutableLiveData<UiUser>()
    val relationship = MutableLiveData<IRelationship>()
    val isMe = MutableLiveData(false)

    suspend fun init(data: UiUser) {
        if (loaded.value == true) {
            return
        }
        user.postValue(data)
        val name = data.screenName
        val key = UserKey(name, "twitter.com")
        isMe.postValue(accountRepository.getCurrentAccount().key != key)
        repository.lookupUser(data.id)?.let {
            user.postValue(it)
        }
        if (accountRepository.getCurrentAccount().key != key) {
            repository.showRelationship(data.id)?.let {
                relationship.postValue(it)
            }
        }
        loaded.postValue(true)
    }
}

