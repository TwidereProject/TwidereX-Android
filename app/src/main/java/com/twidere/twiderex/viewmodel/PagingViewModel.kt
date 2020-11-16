package com.twidere.twiderex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.twidere.twiderex.model.ui.UiStatus.Companion.toUi
import com.twidere.twiderex.paging.mediator.PagingMediator
import com.twidere.twiderex.paging.mediator.pager
import kotlinx.coroutines.flow.map

abstract class PagingViewModel : ViewModel() {
    abstract val pagingMediator: PagingMediator

    val source by lazy {
        pagingMediator.pager().flow.map { pagingData ->
            pagingData.map {
                it.toUi(pagingMediator.userKey)
            }
        }.cachedIn(viewModelScope)
    }
}