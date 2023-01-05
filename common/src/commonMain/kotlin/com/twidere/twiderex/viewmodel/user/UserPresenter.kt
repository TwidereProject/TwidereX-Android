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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.model.IRelationship
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.extensions.rememberNestedPresenter
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.UserRepository
import com.twidere.twiderex.scenes.CurrentAccountPresenter
import com.twidere.twiderex.scenes.CurrentAccountState
import com.twidere.twiderex.utils.notifyError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID

@Composable
fun UserPresenter(
  event: Flow<UserEvent>,
  repository: UserRepository = get(),
  inAppNotification: InAppNotification = get(),
  userKey: MicroBlogKey,
): UserState {

  val currentAccount = CurrentAccountPresenter()
  if (currentAccount !is CurrentAccountState.Account) {
    return UserState.NoAccount
  }

  val account = remember(currentAccount) {
    currentAccount.account
  }

  var refreshing by remember {
    mutableStateOf(false)
  }

  var loadingRelationship by remember {
    mutableStateOf(false)
  }

  val user by remember(userKey) {
    repository.getUserFlow(userKey)
  }.collectAsState(null)

  var refreshFlow by remember {
    mutableStateOf(UUID.randomUUID())
  }

  var relationship by remember {
    mutableStateOf<IRelationship?>(null)
  }

  val isMe = remember(account) {
    account.accountKey == userKey
  }

  LaunchedEffect(account, refreshFlow) {
    loadingRelationship = true
    val relationshipService =
      account.service as RelationshipService
    relationship = runCatching {
      relationshipService.showRelationship(userKey.id)
    }.onSuccess {
      loadingRelationship = false
    }.onFailure {
      loadingRelationship = false
    }.getOrNull()
  }

  LaunchedEffect(account, refreshFlow) {
    refreshing = true
    runCatching {
      repository.lookupUserById(
        userKey.id,
        accountKey = account.accountKey,
        lookupService = account.service as LookupService,
      )
    }.onFailure {
      inAppNotification.notifyError(it)
    }
    refreshing = false
  }

  suspend fun consumeUserEvent(
    block: suspend (RelationshipService) -> Unit
  ) {
    val relationshipService =
      account.service as? RelationshipService ?: return
    loadingRelationship = true
    try {
      block.invoke(relationshipService)
      refreshFlow = UUID.randomUUID()
    } catch (e: Throwable) {
      inAppNotification.notifyError(e)
    } finally {
      loadingRelationship = false
    }
  }

  val (userTimelineState, userTimelineEvent) = key(account) {
    rememberNestedPresenter<UserTimelineState, UserTimelineEvent> {
      UserTimelinePresenter(it, userKey = userKey)
    }
  }

  val userMediaTimelineState = key(account) {
    UserMediaTimelinePresenter(userKey = userKey)
  }

  val userFavouriteTimelineState = key(account) {
    UserFavouriteTimelinePresenter(userKey = userKey)
  }

  LaunchedEffect(Unit) {
    event.collectLatest {
      when (it) {
        UserEvent.Follow -> {
          consumeUserEvent {
            it.follow(userKey.id)
          }
        }
        UserEvent.UnFollow -> {
          consumeUserEvent {
            it.unfollow(userKey.id)
          }
        }
        UserEvent.Block -> {
          consumeUserEvent {
            it.block(id = userKey.id)
          }
        }
        UserEvent.UnBlock -> {
          consumeUserEvent {
            it.unblock(id = userKey.id)
          }
        }
        UserEvent.Refresh -> {
          refreshFlow = UUID.randomUUID()
        }
        is UserEvent.ExcludeReplies -> {
          userTimelineEvent.trySend(
            UserTimelineEvent.ExcludeReplies(it.exclude)
          )
        }
      }
    }
  }

  return UserState.Data(
    refreshing = refreshing,
    loadingRelationship = loadingRelationship,
    user = user,
    relationship = relationship,
    isMe = isMe,
    userTimelineState = userTimelineState,
    userFavouriteTimelineState = userFavouriteTimelineState,
    userMediaTimelineState = userMediaTimelineState,
  )
}

interface UserEvent {
  object Follow : UserEvent
  object UnFollow : UserEvent
  object Block : UserEvent
  object UnBlock : UserEvent
  object Refresh : UserEvent
  data class ExcludeReplies(
    val exclude: Boolean
  ) : UserEvent
}

interface UserState {
  data class Data(
    val refreshing: Boolean,
    val loadingRelationship: Boolean,
    val user: UiUser?,
    val relationship: IRelationship?,
    val isMe: Boolean,
    val userTimelineState: UserTimelineState,
    val userFavouriteTimelineState: UserFavouriteTimelineState,
    val userMediaTimelineState: UserMediaTimelineState,
  ) : UserState

  object NoAccount : UserState
}
