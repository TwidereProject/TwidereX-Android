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
package com.twidere.twiderex.component.lazy.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.twidere.twiderex.component.lazy.loadState
import com.twidere.twiderex.component.placeholder.UiUserPlaceholder
import com.twidere.twiderex.component.status.HtmlText
import com.twidere.twiderex.component.status.ResolvedLink
import com.twidere.twiderex.component.status.RoundAvatar
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.compose.LocalResLoader
import com.twidere.twiderex.kmp.ResLoader
import com.twidere.twiderex.model.ui.UiDMConversationWithLatestMessage
import com.twidere.twiderex.model.ui.UiDMEvent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LazyUiDMConversationList(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<UiDMConversationWithLatestMessage>,
    state: LazyListState = rememberLazyListState(),
    key: ((item: UiDMConversationWithLatestMessage) -> Any) = { it.conversation.conversationKey.hashCode() },
    onItemClicked: (UiDMConversationWithLatestMessage) -> Unit = {},
    header: LazyListScope.() -> Unit = {},
    action: @Composable (user: UiDMConversationWithLatestMessage) -> Unit = {}
) {
    val resLoader = LocalResLoader.current
    LazyUiList(items = items) {
        LazyColumn(
            modifier = modifier,
            state = state,
        ) {
            header.invoke(this)
            items(
                items,
                key = key
            ) {
                it?.let {
                    Row(
                        modifier = Modifier.clickable {
                            onItemClicked.invoke(it)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ListItem(
                            modifier = Modifier.weight(1f),
                            icon = {
                                RoundAvatar(
                                    avatar = it.conversation.conversationAvatar,
                                    onClick = {
                                        onItemClicked(it)
                                    }
                                )
                            },
                            text = {
                                Row {
                                    UserName(userName = it.conversation.conversationName)
                                    Spacer(
                                        modifier = Modifier.width(
                                            LazyUiDMConversationListDefaults.HorizontalPadding
                                        )
                                    )
                                    UserScreenName(name = it.conversation.conversationSubName)
                                }
                            },
                            secondaryText = {
                                HtmlText(
                                    htmlText = it.latestMessage.htmlText,
                                    maxLines = 1,
                                    linkResolver = { href ->
                                        it.latestMessage.resolveLink(
                                            href,
                                            resLoader
                                        )
                                    },
                                )
                            },
                        )
                        Box(modifier = Modifier.padding(end = LazyUiDMConversationListDefaults.TrailingRightPadding)) {
                            action.invoke(it)
                        }
                    }
                } ?: run {
                    LoadingUserPlaceholder()
                }
            }
            loadState(items.loadState.append) {
                items.retry()
            }
        }
    }
}

object LazyUiDMConversationListDefaults {
    val HorizontalPadding = 8.dp
    val TrailingRightPadding = 16.dp
}

@Composable
private fun LoadingUserPlaceholder() {
    Column(
        modifier = Modifier
            .wrapContentHeight(
                align = Alignment.Top,
                unbounded = true
            )
    ) {
        repeat(10) {
            UiUserPlaceholder(
                delayMillis = it * 50L
            )
        }
    }
}

private fun UiDMEvent.resolveLink(
    href: String,
    resLoader: ResLoader,
): ResolvedLink {
    val entity = urlEntity.firstOrNull { it.url == href }
    val media = media.firstOrNull { it.url == href }
    return when {
        media != null -> {
            ResolvedLink(
                expanded = "[${media.type.name}]",
                clickable = false
            )
        }
        entity != null -> {
            if (entity.displayUrl.contains("pic.twitter.com")) {
                ResolvedLink(
                    expanded = resLoader.getString(com.twidere.twiderex.MR.strings.scene_messages_expanded_photo),
                    clickable = false
                )
            } else {
                ResolvedLink(
                    expanded = entity.expandedUrl,
                    display = entity.displayUrl,
                    clickable = false
                )
            }
        }
        else -> {
            ResolvedLink(expanded = null, clickable = false)
        }
    }
}
