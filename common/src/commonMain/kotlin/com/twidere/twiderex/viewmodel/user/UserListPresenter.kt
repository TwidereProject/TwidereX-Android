/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
 *
 *  This file is part of Twidere X.
 *
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.viewmodel.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.twidere.services.microblog.ListsService
import com.twidere.services.microblog.RelationshipService
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.repository.ListsUsersRepository
import com.twidere.twiderex.repository.UserListRepository
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import kotlinx.coroutines.flow.Flow

interface UserListType {
  data class Following(
    val userKey: MicroBlogKey
  ) : UserListType
  data class Followers(
    val userKey: MicroBlogKey,
  ) : UserListType
  data class ListUsers(
    val listId: String,
    val viewMembers: Boolean = true,
  ) : UserListType
}

@Composable
fun UserListPresenter(
  event: Flow<UserListEvent>,
  userType: UserListType,
  repository: UserListRepository = get(),
  listsUsersRepository: ListsUsersRepository = get(),
): UserListState {
  val currentAccount = CurrentAccountPresenter()

  if (currentAccount !is CurrentAccountState.Account) {
    return UserListState.NoAccount
  }

  if (userType is UserListType.ListUsers) {
    LaunchedEffect(Unit) {
      event.collect {
        when (it) {
          is UserListEvent.RemoveMember -> {
            listsUsersRepository.removeMember(
              service = currentAccount.account.service as ListsService,
              listId = userType.listId,
              user = it.user
            )
          }
        }
      }
    }
  }

  val source = remember(currentAccount) {
    when (userType) {
      is UserListType.ListUsers -> {
        if (userType.viewMembers) {
          listsUsersRepository.fetchMembers(
            accountKey = currentAccount.account.accountKey,
            service = currentAccount.account.service as ListsService,
            listId = userType.listId
          )
        } else {
          listsUsersRepository.fetchSubscribers(
            accountKey = currentAccount.account.accountKey,
            service = currentAccount.account.service as ListsService,
            listId = userType.listId
          )
        }
      }
      is UserListType.Followers -> {
        repository.followers(
          userType.userKey,
          currentAccount.account.service as RelationshipService
        )
      }
      is UserListType.Following -> {
        repository.following(
          userType.userKey,
          currentAccount.account.service as RelationshipService
        )
      }
      else -> { throw Exception("UserListType not correct") }
    }
  }.collectAsLazyPagingItems()

  return UserListState.Data(source = source)
}

interface UserListEvent {
  data class RemoveMember(
    val user: UiUser
  ) : UserListEvent
}

interface UserListState {
  data class Data(
    val source: LazyPagingItems<UiUser>
  ) : UserListState

  object NoAccount : UserListState
}
