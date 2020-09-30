package com.twidere.twiderex.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.model.IRelationship
import com.twidere.twiderex.db.mapper.toDbUser
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import com.twidere.twiderex.repository.AccountRepository

class UserViewModel @ViewModelInject constructor(
    private val repository: AccountRepository,
) : ViewModel() {
    val loaded = MutableLiveData(false)
    val user = MutableLiveData<UiUser>()
    val relationship = MutableLiveData<IRelationship>()

    private val lookupService by lazy {
        repository.getCurrentAccount().let { account ->
            account.service.let {
                it as? LookupService
            }
        }
    }

    private val relationshipService by lazy {
        repository.getCurrentAccount().let { account ->
            account.service.let {
                it as? RelationshipService
            }
        }
    }

    suspend fun init(data: UiUser) {
        if (loaded.value == true) {
            return
        }
        user.postValue(data)
        lookupService?.lookupUser(data.id)?.toDbUser()?.toUi()?.let {
            user.postValue(it)
        }
        relationshipService?.showRelationship(data.id)?.let {
            relationship.postValue(it)
        }
        loaded.postValue(true)
    }
}