/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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

import android.text.format.DateUtils
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.twidere.twiderex.component.lazy.loadState
import com.twidere.twiderex.component.placeholder.UiUserPlaceholder
import com.twidere.twiderex.component.status.HtmlText
import com.twidere.twiderex.component.status.ResolvedLink
import com.twidere.twiderex.component.status.StatusMediaDefaults
import com.twidere.twiderex.component.status.StatusMediaPreviewItem
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.UserAvatarDefaults
import com.twidere.twiderex.model.ui.UiDMEvent
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.ui.LocalVideoPlayback

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LazyUiDMEventList(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<UiDMEvent>,
    state: LazyListState = rememberLazyListState(),
    key: ((index: Int) -> Any) = { items.peek(it)?.messageKey?.hashCode() ?: it },
    header: LazyListScope.() -> Unit = {},
    action: @Composable (user: UiDMEvent) -> Unit = {}
) {
    LazyUiList(items = items) {
        LazyColumn(
            modifier = modifier,
            state = state,
            reverseLayout = true
        ) {
            header.invoke(this)
            items(
                items,
                key = key
            ) {
                it?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(LazyUiDMEventListDefaults.ContentPadding)
                    ) {
                        if (it.isInCome)
                            DMInComeEvent(it)
                        else
                            DMOutComeEvent(action, it)
                    }
                } ?: run {
                    LoadingEventPlaceholder()
                }
            }
            loadState(items.loadState.append) {
                items.retry()
            }
        }
    }
}

private object LazyUiDMEventListDefaults {
    val ContentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
}

@Composable
private fun DMOutComeEvent(action: @Composable (event: UiDMEvent) -> Unit = {}, event: UiDMEvent) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.Bottom) {
                action.invoke(event)
                Spacer(modifier = Modifier.width(DMEventDefaults.ContentSpacing))
                MessageBody(event)
            }
            ChatTime(
                modifier = Modifier.padding(
                    top = DMEventDefaults.Time.paddingTop
                ),
                time = event.createdTimestamp
            )
        }
    }
}

@Composable
private fun DMInComeEvent(event: UiDMEvent) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                UserAvatar(user = event.sender)
                Spacer(modifier = Modifier.width(DMEventDefaults.ContentSpacing))
                MessageBody(event)
            }
            ChatTime(
                modifier = Modifier.padding(
                    top = DMEventDefaults.Time.paddingTop,
                    start = DMEventDefaults.Time.paddingStart
                ),
                time = event.createdTimestamp
            )
        }
    }
}

private object DMEventDefaults {
    val ContentSpacing = 10.dp
    object Time {
        val paddingTop = 8.dp
        val paddingStart = UserAvatarDefaults.AvatarSize + ContentSpacing
    }
}

@Composable
private fun MessageBody(event: UiDMEvent) {
    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topStart = MessageBodyDefaults.cornerRadius,
                    topEnd = MessageBodyDefaults.cornerRadius,
                    bottomStart = if (event.isInCome) MessageBodyDefaults.pointCornerRadius else MessageBodyDefaults.cornerRadius,
                    bottomEnd = if (event.isInCome) MessageBodyDefaults.cornerRadius else MessageBodyDefaults.pointCornerRadius
                )
            )
            .background(
                if (event.isInCome)
                    MaterialTheme.colors.primary.copy(alpha = 0.15f)
                else
                    MaterialTheme.colors.primary
            )
            .padding(MessageBodyDefaults.ContentPadding)
    ) {
        Column {
            MediaMessage(media = event.media.firstOrNull())
            val textColor = if (event.isInCome) MaterialTheme.colors.onSurface else MaterialTheme.colors.onPrimary
            CompositionLocalProvider(LocalContentColor provides textColor) {
                HtmlText(htmlText = event.htmlText, linkResolver = { href -> event.resolveLink(href) })
            }
        }
    }
}

private object MessageBodyDefaults {
    val cornerRadius = 8.dp
    val pointCornerRadius = 2.dp
    val ContentPadding = PaddingValues(12.dp)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun MediaMessage(media: UiMedia?) {
    // TODO DM fixme photo with ton.twitter.api needs to be authed
    media?.let { item ->
        // val navigator = LocalNavigator.current
        val aspectRatio = (item.width.toFloat() / item.height.toFloat()).let {
            if (it.isNaN()) {
                StatusMediaDefaults.DefaultAspectRatio
            } else {
                it
            }
        }
        CompositionLocalProvider(LocalVideoPlayback provides DisplayPreferences.AutoPlayback.Off) {
            StatusMediaPreviewItem(
                media = item,
                modifier = Modifier
                    .heightIn(max = StatusMediaDefaults.DefaultMaxHeight)
                    .aspectRatio(aspectRatio),
                onClick = {
                    // todo DM navigate to media scene
                }
            )
        }
    }
}

@Composable
private fun ChatTime(modifier: Modifier = Modifier, time: Long) {
    val context = LocalContext.current
    val timeString = remember(time) {
        DateUtils.getRelativeDateTimeString(
            context,
            time,
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.DAY_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_ALL
        ).toString()
    }
    Text(modifier = modifier, text = timeString, style = MaterialTheme.typography.overline)
}

@Composable
private fun LoadingEventPlaceholder() {
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
): ResolvedLink {
    val entity = urlEntity.firstOrNull { it.url == href }
    val media = media.firstOrNull { it.url == href }
    return when {
        media != null -> {
            ResolvedLink(expanded = null, skip = true)
        }
        entity != null -> {
            ResolvedLink(expanded = entity.expandedUrl, display = entity.displayUrl)
        }
        else -> {
            ResolvedLink(expanded = null)
        }
    }
}
