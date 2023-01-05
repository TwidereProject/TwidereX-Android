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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material.icons.twotone.RemoveCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.MR
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.status.RetweetHeader
import com.twidere.twiderex.component.status.StatusContentDefaults
import com.twidere.twiderex.component.status.StatusHeader
import com.twidere.twiderex.component.status.StatusHeaderDefaults
import com.twidere.twiderex.component.status.TokenText
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.extensions.icon
import com.twidere.twiderex.model.enums.MastodonNotificationType
import com.twidere.twiderex.model.ui.UiFollow
import com.twidere.twiderex.model.ui.UiFollowRequest
import com.twidere.twiderex.model.ui.UiGap
import com.twidere.twiderex.model.ui.UiMastodonStatus
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.model.ui.UiRetweetStatus
import com.twidere.twiderex.model.ui.UiStatusTimeline
import com.twidere.twiderex.model.ui.UiStatusWithCard
import com.twidere.twiderex.model.ui.UiStatusWithMedia
import com.twidere.twiderex.model.ui.UiStatusWithPoll
import com.twidere.twiderex.model.ui.UiStatusWithQuote
import com.twidere.twiderex.model.ui.UiStatusWithRetweetAndQuote
import com.twidere.twiderex.model.ui.UiTimeline
import com.twidere.twiderex.model.ui.UiTwitterStatus
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.resolveLink
import com.twidere.twiderex.ui.LocalActiveAccount
import kotlinx.collections.immutable.ImmutableList
import moe.tlaster.twitter.parser.Token

@Composable
fun LazyListScope.LazyUiTimelineList() {
}

@Composable
fun UiTimelineComponent(
  timeline: UiTimeline,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  when (timeline) {
    is UiFollow -> {
      UiFollowComponent(
        data = timeline,
        clickable = clickable,
        modifier = modifier,
      )
    }

    is UiFollowRequest -> {
      UiFollowRequestComponent(
        data = timeline,
        clickable = clickable,
        modifier = modifier,
      )
    }

    is UiGap -> {
      UiGapComponent(
        data = timeline,
        clickable = clickable,
        modifier = modifier,
      )
    }

    is UiMastodonStatus -> {
      UiMastodonStatusComponent(
        data = timeline,
        clickable = clickable,
        modifier = modifier,
      )
    }

    is UiRetweetStatus -> TODO()
    is UiTwitterStatus -> TODO()
    is UiStatusWithCard -> TODO()
    is UiStatusWithMedia -> TODO()
    is UiStatusWithPoll -> TODO()
    is UiStatusWithQuote -> TODO()
    is UiStatusWithRetweetAndQuote -> TODO()
  }
}

object TimelineComponentDefaults {
  val AvatarSpacing = 10.dp
}

@Composable
fun UiMastodonStatusComponent(
  data: UiMastodonStatus,
  clickable: TimelineClickable,
  extraContent: @Composable () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
  ) {
    if (data.notificationType != null) {
      MastodonNotificationHeader(
        data = data,
        clickable = clickable,
      )
      Spacer(modifier = Modifier.height(StatusHeaderDefaults.HeaderSpacing))
    }
    Row {
      UserAvatar(
        user = data.user,
        onClick = clickable.onUserClicked,
      )
      Spacer(modifier = Modifier.width(TimelineComponentDefaults.AvatarSpacing))
      Column {
        Row(
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            UserName(
              data.user,
              fontWeight = FontWeight.W600,
              onUserNameClicked = clickable.onLinkClicked,
            )
            Spacer(modifier = Modifier.width(6.dp))
            UserScreenName(data.user)
          }
          CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.disabled,
          ) {
            Icon(
              modifier = Modifier.size(LocalTextStyle.current.fontSize.value.dp),
              painter = data.visibility.icon(),
              contentDescription = data.visibility.name,
            )
            Spacer(modifier = Modifier.width(StatusContentDefaults.Mastodon.VisibilitySpacing))
            Text(data.humanizedTime)
          }
        }
        if (data.spoilerText != null) {
          MastodonContentWithSpoiler(
            data = data,
            clickable = clickable,
          )
        } else {
          MastodonContent(
            data = data,
            clickable = clickable,
            token = data.parsedContent,
          )
        }
        extraContent.invoke()
        UiStatusMetricsComponent(
          status = data,
          clickable = clickable,
        )
      }
    }
  }
}

@Composable
fun UiStatusMetricsComponent(
  status: UiStatusTimeline,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
  }
}

@Composable
fun MetricsButton(
  onClick: () -> Unit,
  icon: Painter,
  showNumbers: Boolean,
  countString: String,
  modifier: Modifier = Modifier,
  contentDescription: String? = null,
) {
  Row(
    modifier = modifier
      .clickable {
        onClick.invoke()
      },
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      modifier = Modifier
        .size(MaterialTheme.typography.caption.fontSize.value.dp),
      painter = icon,
      contentDescription = contentDescription,
    )
    if (showNumbers) {
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = countString,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.caption,
      )
    }
  }
}

@Composable
fun MastodonContent(
  token: ImmutableList<Token>,
  data: UiMastodonStatus,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  TokenText(
    token = token,
    layoutDirection = data.contentDirection,
    onLinkClicked = clickable.onLinkClicked,
    modifier = modifier,
    linkResolver = {
      data.resolveLink(it)
    },
    emojiResolver = { emoji ->
      data.emoji.firstOrNull { it.shortcode == emoji.trim(':') }?.url ?: emoji
    },
  )
}

@Composable
fun MastodonContentWithSpoiler(
  data: UiMastodonStatus,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
  ) {
    MastodonContent(
      data = data,
      clickable = clickable,
      token = data.parsedSpoilerText,
    )
    Row(
      modifier = Modifier
        .size(width = 46.dp, height = 20.dp)
        .clickable {
          if (data.expanded) {
            clickable.onCollapseClicked(data)
          } else {
            clickable.onExpandClicked(data)
          }
        }
        .background(remember { Color.Black.copy(alpha = 0.04f) }, shape = RoundedCornerShape(8.dp))
        .clip(RoundedCornerShape(8.dp)),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
    ) {
      Box(modifier = Modifier.background(MaterialTheme.colors.primary, shape = CircleShape))
      Box(modifier = Modifier.background(MaterialTheme.colors.primary, shape = CircleShape))
      Box(modifier = Modifier.background(MaterialTheme.colors.primary, shape = CircleShape))
    }
    AnimatedVisibility(data.expanded) {
      MastodonContent(
        data = data,
        clickable = clickable,
        token = data.parsedContent,
      )
    }
  }
}

@Composable
fun MastodonNotificationHeader(
  data: UiMastodonStatus,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  when (data.notificationType) {
    MastodonNotificationType.Follow -> {
      StatusHeader(
        modifier = modifier,
        icon = {
          Icon(
            painter = painterResource(res = MR.files.ic_user_plus),
            contentDescription = null,
          )
        },
        text = {
          Text(
            text = stringResource(
              res = MR.strings.common_notification_follow,
              data.user.displayName,
            ),
          )
        },
      )
    }

    MastodonNotificationType.FollowRequest -> {
      StatusHeader(
        modifier = modifier,
        icon = {
          Icon(
            painter = painterResource(res = MR.files.ic_user_exclamation),
            contentDescription = null,
          )
        },
        text = {
          Text(
            text = stringResource(
              res = MR.strings.common_notification_follow_request,
              data.user.displayName,
            ),
          )
        },
      )
    }

    MastodonNotificationType.Mention -> Unit
    MastodonNotificationType.Reblog -> {
      RetweetHeader(
        name = data.user.displayName,
        openLink = clickable.onLinkClicked,
        modifier = modifier,
      )
    }

    MastodonNotificationType.Favourite -> {
      StatusHeader(
        modifier = modifier,
        icon = {
          Icon(
            painter = painterResource(res = MR.files.ic_heart),
            contentDescription = null,
          )
        },
        text = {
          Text(
            text = stringResource(
              res = MR.strings.common_notification_favourite,
              data.user.displayName,
            ),
          )
        },
      )
    }

    MastodonNotificationType.Poll -> {
      StatusHeader(
        modifier = modifier,
        icon = {
          Icon(
            painter = painterResource(res = MR.files.ic_poll),
            contentDescription = null,
          )
        },
        text = {
          val text =
            if (LocalActiveAccount.current?.let { it.accountKey == data.user.userKey } == true) {
              stringResource(
                res = MR.strings.common_notification_own_poll,
              )
            } else {
              stringResource(
                res = MR.strings.common_notification_poll,
              )
            }

          Text(
            text = text,
          )
        },
      )
    }

    MastodonNotificationType.Status -> {
      StatusHeader(
        modifier = modifier,
        icon = {
          Icon(
            painter = painterResource(res = MR.files.ic_bell_ringing),
            contentDescription = null,
          )
        },
        text = {
          Text(
            text = stringResource(
              res = MR.strings.common_notification_status,
              data.user.displayName,
            ),
          )
        },
      )
    }

    null -> Unit
  }
}

@Composable
fun UiGapComponent(
  data: UiGap,
  clickable: TimelineClickable,
  modifier: Modifier,
) {
  Box(
    modifier = modifier
      .background(MaterialTheme.colors.surface)
      .clickable { clickable.onGapClicked(data) }
      .fillMaxWidth(),
    contentAlignment = Alignment.Center,
  ) {
    if (data.loading) {
      CircularProgressIndicator()
    } else {
      Text(
        modifier = Modifier.padding(12.dp),
        text = stringResource(
          res = MR.strings.common_controls_timeline_load_more,
        ),
        color = MaterialTheme.colors.primary,
      )
    }
  }
}

@Composable
fun UiFollowComponent(
  data: UiFollow,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
  ) {
    StatusHeader(
      icon = {
        Icon(
          painter = painterResource(res = MR.files.ic_user_plus),
          contentDescription = null,
        )
      },
      text = {
        Text(
          text = stringResource(
            res = MR.strings.common_notification_follow,
            data.user.displayName,
          ),
        )
      },
    )

    Row {
      UserAvatar(
        user = data.user,
        onClick = {
          clickable.onUserClicked(data.user)
        },
      )
      Spacer(modifier = Modifier.width(TimelineComponentDefaults.AvatarSpacing))
      Column {
        Text(
          text = data.user.displayName,
          maxLines = 1,
          style = MaterialTheme.typography.subtitle1,
        )
        Text(
          text = data.user.screenName,
          maxLines = 1,
          style = MaterialTheme.typography.caption,
        )
      }
    }
  }
}

@Composable
fun UiFollowRequestComponent(
  data: UiFollowRequest,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
  ) {
    StatusHeader(
      icon = {
        Icon(
          painter = painterResource(res = MR.files.ic_user_plus),
          contentDescription = null,
        )
      },
      text = {
        Text(
          text = stringResource(
            res = MR.strings.common_notification_follow_request,
            data.user.displayName,
          ),
        )
      },
    )

    Row {
      UserAvatar(
        user = data.user,
        onClick = {
          clickable.onUserClicked(data.user)
        },
      )
      Spacer(modifier = Modifier.width(TimelineComponentDefaults.AvatarSpacing))
      Column(
        modifier = Modifier.weight(1f),
      ) {
        Text(
          text = data.user.displayName,
          maxLines = 1,
          style = MaterialTheme.typography.subtitle1,
        )
        Text(
          text = data.user.screenName,
          maxLines = 1,
          style = MaterialTheme.typography.caption,
        )
      }
      IconButton(
        onClick = {
          clickable.onAcceptFollowClicked(data)
        },
      ) {
        Icon(
          Icons.TwoTone.CheckCircle,
          contentDescription = null,
          tint = MaterialTheme.colors.primary,
        )
      }
      IconButton(
        onClick = {
          clickable.onRejectFollowClicked(data)
        },
      ) {
        Icon(
          Icons.TwoTone.RemoveCircle,
          contentDescription = null,
        )
      }
    }
  }
}

data class TimelineClickable(
  val onUserClicked: (UiUser) -> Unit,
  val onStatusClicked: (UiStatusTimeline) -> Unit,
  val onMediaClicked: (UiMedia) -> Unit,
  val onRetweetClicked: (UiStatusTimeline) -> Unit,
  val onUnretweetClicked: (UiStatusTimeline) -> Unit,
  val onLikeClicked: (UiStatusTimeline) -> Unit,
  val onUnlikeClicked: (UiStatusTimeline) -> Unit,
  val onGapClicked: (UiGap) -> Unit,
  val onAcceptFollowClicked: (UiFollowRequest) -> Unit,
  val onRejectFollowClicked: (UiFollowRequest) -> Unit,
  val onLinkClicked: (String) -> Unit,
  val onExpandClicked: (UiStatusTimeline) -> Unit,
  val onCollapseClicked: (UiStatusTimeline) -> Unit,
) {
  companion object {
    val Empty = TimelineClickable(
      onUserClicked = {},
      onStatusClicked = {},
      onMediaClicked = {},
      onRetweetClicked = {},
      onUnretweetClicked = {},
      onLikeClicked = {},
      onUnlikeClicked = {},
      onGapClicked = {},
      onAcceptFollowClicked = {},
      onRejectFollowClicked = {},
      onLinkClicked = {},
      onExpandClicked = {},
      onCollapseClicked = {},
    )
  }
}
