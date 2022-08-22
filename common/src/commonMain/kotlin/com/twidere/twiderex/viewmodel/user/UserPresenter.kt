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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.twidere.services.microblog.LookupService
import com.twidere.services.microblog.RelationshipService
import com.twidere.services.microblog.model.IRelationship
import com.twidere.twiderex.di.ext.get
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

  val isMe by remember(currentAccount) {
    derivedStateOf { currentAccount.account.accountKey == userKey }
  }

  LaunchedEffect(currentAccount, refreshFlow) {
    loadingRelationship = true
    val relationshipService = currentAccount.account.service as RelationshipService
    relationship = runCatching {
      relationshipService.showRelationship(userKey.id)
    }.onSuccess {
      loadingRelationship = false
    }.onFailure {
      loadingRelationship = false
    }.getOrNull()
  }

  LaunchedEffect(currentAccount, refreshFlow) {
    refreshing = true
    runCatching {
      repository.lookupUserById(
        userKey.id,
        accountKey = currentAccount.account.accountKey,
        lookupService = currentAccount.account.service as LookupService,
      )
    }.onFailure {
      inAppNotification.notifyError(it)
    }
    refreshing = false
  }

  LaunchedEffect(Unit) {
    event.collectLatest {
      when (it) {
        UserEvent.Follow -> {
          loadingRelationship = true
          val relationshipService =
            currentAccount.account.service as? RelationshipService ?: return@collectLatest
          try {
            relationshipService.follow(userKey.id)
            refreshFlow = UUID.randomUUID()
          } catch (e: Throwable) {
            inAppNotification.notifyError(e)
          } finally {
            loadingRelationship = false
          }
        }
        UserEvent.UnFollow -> {
          loadingRelationship = true
          val relationshipService = currentAccount.account.service as? RelationshipService ?: return@collectLatest
          try {
            relationshipService.unfollow(userKey.id)
            refreshFlow = UUID.randomUUID()
          } catch (e: Throwable) {
            inAppNotification.notifyError(e)
          } finally {
            loadingRelationship = false
          }
        }
        UserEvent.Block -> {
          val relationshipService = currentAccount.account.service as? RelationshipService ?: return@collectLatest
          loadingRelationship = true
          try {
            relationshipService.block(id = userKey.id)
            refreshFlow = UUID.randomUUID()
          } catch (e: Throwable) {
            inAppNotification.notifyError(e)
          } finally {
            loadingRelationship = false
          }
        }
        UserEvent.UnBlock -> {
          val relationshipService = currentAccount.account.service as? RelationshipService ?: return@collectLatest
          loadingRelationship = true
          try {
            relationshipService.unblock(id = userKey.id)
            refreshFlow = UUID.randomUUID()
          } catch (e: Throwable) {
            inAppNotification.notifyError(e)
          } finally {
            loadingRelationship = false
          }
        }
        UserEvent.Refresh -> {
          refreshFlow = UUID.randomUUID()
        }
      }
    }
  }

  return UserState.Data(
    refreshing = refreshing,
    loadingRelationship = loadingRelationship,
    user = user,
    relationship = relationship,
    isMe = isMe
  )
}

interface UserEvent {
  object Follow : UserEvent
  object UnFollow : UserEvent
  object Block : UserEvent
  object UnBlock : UserEvent
  object Refresh : UserEvent
}

interface UserState {
  data class Data(
    val refreshing: Boolean,
    val loadingRelationship: Boolean,
    val user: UiUser?,
    val relationship: IRelationship?,
    val isMe: Boolean,
  ) : UserState

  object NoAccount : UserState
}
