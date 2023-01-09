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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material.icons.twotone.RemoveCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.twidere.twiderex.MR
import com.twidere.twiderex.component.foundation.DropdownMenu
import com.twidere.twiderex.component.foundation.DropdownMenuItem
import com.twidere.twiderex.component.foundation.GridLayout
import com.twidere.twiderex.component.painterResource
import com.twidere.twiderex.component.placeholder.UiStatusPlaceholder
import com.twidere.twiderex.component.status.LinkPreview
import com.twidere.twiderex.component.status.MediaPreviewButtonDefaults
import com.twidere.twiderex.component.status.RetweetHeader
import com.twidere.twiderex.component.status.StatusContentDefaults
import com.twidere.twiderex.component.status.StatusHeader
import com.twidere.twiderex.component.status.StatusHeaderDefaults
import com.twidere.twiderex.component.status.StatusMediaPreviewItem
import com.twidere.twiderex.component.status.TokenText
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.UserAvatarDefaults
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
import com.twidere.twiderex.model.ui.UiPoll
import com.twidere.twiderex.model.ui.UiPollOption
import com.twidere.twiderex.model.ui.UiQuoteStatus
import com.twidere.twiderex.model.ui.UiRetweetAndQuoteStatus
import com.twidere.twiderex.model.ui.UiRetweetStatus
import com.twidere.twiderex.model.ui.UiStatusMetaData
import com.twidere.twiderex.model.ui.UiStatusTimeline
import com.twidere.twiderex.model.ui.UiStatusWithCard
import com.twidere.twiderex.model.ui.UiStatusWithExtra
import com.twidere.twiderex.model.ui.UiStatusWithMedia
import com.twidere.twiderex.model.ui.UiStatusWithPoll
import com.twidere.twiderex.model.ui.UiTimeline
import com.twidere.twiderex.model.ui.UiTwitterStatus
import com.twidere.twiderex.model.ui.UiTwitterThreadStatus
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.preferences.LocalDisplayPreferences
import com.twidere.twiderex.ui.TwidereTheme
import kotlinx.collections.immutable.ImmutableList
import moe.tlaster.twitter.parser.Token

fun LazyListScope.lazyUiTimelineList(
  items: LazyPagingItems<UiTimeline>,
  clickable: TimelineClickable,
  contentPadding: PaddingValues = PaddingValues(0.dp),
) {
  items(items) {
    if (it == null) {
      UiStatusPlaceholder()
    } else {
      UiTimelineComponent(
        timeline = it,
        clickable = clickable,
        modifier = Modifier.padding(contentPadding),
      )
    }
  }
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

    is UiStatusTimeline -> {
      UiStatusTimelineComponent(
        data = timeline,
        clickable = clickable,
        modifier = modifier,
      )
    }

    is UiStatusWithExtra -> {
      UiStatusWithExtraComponent(
        data = timeline,
        clickable = clickable,
        modifier = modifier,
      )
    }

    is UiRetweetStatus -> {
      UiRetweetStatusComponent(
        data = timeline,
        clickable = clickable,
        modifier = modifier,
      )
    }

    is UiQuoteStatus -> {
      UiQuoteStatusComponent(
        data = timeline,
        clickable = clickable,
        modifier = modifier,
      )
    }

    is UiRetweetAndQuoteStatus -> {
      UiRetweetAndQuoteStatusComponent(
        data = timeline,
        clickable = clickable,
        modifier = modifier,
      )
    }

    is UiTwitterThreadStatus -> {
      UiTwitterThreadStatusComponent(
        data = timeline,
        clickable = clickable,
        modifier = modifier,
      )
    }
  }
}

object UiTimelineDefaults {
  val LineColor
    @Composable
    get() = LocalContentColor.current.copy(alpha = 0.12f)
}

@Composable
fun UiTwitterThreadStatusComponent(
  data: UiTwitterThreadStatus,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .height(IntrinsicSize.Min),
  ) {
    Column {
      UiTwitterStatusComponent(
        data = data.status,
        clickable = clickable,
      )
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clickable {
            clickable.onStatusClicked.invoke(data.status)
          },
      ) {
        UserAvatar(
          modifier = Modifier.padding(
            start = 6.dp,
          ),
          user = data.status.data.user,
          onClick = {
            clickable.onStatusClicked.invoke(data.status)
          },
          size = UiTwitterThreadStatusDefaults.ThreadAvatarSize,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
          text = stringResource(MR.strings.common_controls_status_thread_show),
          style = MaterialTheme.typography.body2,
          color = MaterialTheme.colors.primary,
        )
      }
    }

    Box(
      modifier = modifier
        .padding(
          start = remember { (UserAvatarDefaults.AvatarSize) / 2 },
          bottom = remember { (UiTwitterThreadStatusDefaults.ThreadAvatarSize) / 2 },
        )
        .width(2.dp)
        .background(UiTimelineDefaults.LineColor)
        .fillMaxHeight(),
    )
  }
}

object UiTwitterThreadStatusDefaults {
  val ThreadAvatarSize = 32.dp
}

@Composable
fun UiRetweetAndQuoteStatusComponent(
  data: UiRetweetAndQuoteStatus,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    RetweetHeader(
      name = data.retweet.status.data.user.displayName,
      openLink = clickable.onLinkClicked,
    )
    UiQuoteStatusComponent(
      data = data.status,
      clickable = clickable,
    )
  }
}

@Composable
fun UiQuoteStatusComponent(
  data: UiQuoteStatus,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  UiStatusWithExtraComponent(
    data = data.status,
    clickable = clickable,
    modifier = modifier,
    extraContent = {
      Spacer(modifier = Modifier.height(10.dp))
      UiQuoteStatusContent(
        data = data.quote,
        clickable = clickable,
      )
    },
  )
}

@Composable
fun UiQuoteStatusContent(
  data: UiStatusWithExtra,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .background(
        LocalContentColor.current.copy(0.04f),
        shape = MaterialTheme.shapes.medium,
      )
      .clickable {
        clickable.onStatusClicked.invoke(data.status)
      }
      .padding(12.dp)
      .clip(MaterialTheme.shapes.medium),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
    ) {
      UserAvatar(
        user = data.status.data.user,
        onClick = {
          clickable.onUserClicked.invoke(data.status.data.user)
        },
        size = 16.dp,
      )
      Spacer(modifier = Modifier.width(8.dp))
      UserName(
        user = data.status.data.user,
        onUserNameClicked = clickable.onLinkClicked,
      )
      Spacer(modifier = Modifier.width(6.dp))
      UserScreenName(
        user = data.status.data.user,
      )
    }
    Spacer(modifier = Modifier.height(4.dp))
    StatusText(
      data = data.status,
      clickable = clickable,
    )
    Spacer(modifier = Modifier.height(8.dp))
    UiStatusWithExtraContent(
      data = data,
      clickable = clickable,
    )
  }
}

@Composable
fun UiStatusWithExtraComponent(
  data: UiStatusWithExtra,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
  extraContent: @Composable ColumnScope.() -> Unit = {},
) {
  UiStatusTimelineComponent(
    data = data.status,
    clickable = clickable,
    modifier = modifier,
    extraContent = {
      Spacer(modifier = Modifier.height(10.dp))
      UiStatusWithExtraContent(data, clickable)
      extraContent.invoke(this)
    },
  )
}

@Composable
private fun ColumnScope.UiStatusWithExtraContent(
  data: UiStatusWithExtra,
  clickable: TimelineClickable
) {
  when (data) {
    is UiStatusWithCard -> {
      UiStatusCardContent(
        data = data,
        clickable = clickable,
      )
    }

    is UiStatusWithMedia -> {
      UiStatusMediaContent(
        status = data,
        clickable = clickable,
      )
    }

    is UiStatusWithPoll -> {
      UiStatusPollContent(
        data = data,
        clickable = clickable,
      )
    }
  }
}

@Composable
fun ColumnScope.UiStatusPollContent(
  data: UiStatusWithPoll,
  clickable: TimelineClickable,
) {
  data.poll.options.forEachIndexed { index, option ->
    PollOption(
      option,
      data.poll,
      onVote = {
        clickable.onVote.invoke(data, option)
      },
    )
    if (index != data.poll.options.lastIndex) {
      Spacer(modifier = Modifier.height(PollDefaults.OptionSpacing))
    }
  }
  Spacer(modifier = Modifier.height(PollDefaults.VoteInfoSpacing))
  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    val countText = data.poll.votersCount?.let {
      if (it > 1) {
        stringResource(
          res = MR.strings.common_controls_status_poll_total_people,
          it,
        )
      } else {
        stringResource(
          res = MR.strings.common_controls_status_poll_total_person,
          it,
        )
      }
    } ?: data.poll.votesCount?.let {
      if (it > 1) {
        stringResource(
          res = MR.strings.common_controls_status_poll_total_votes,
          it,
        )
      } else {
        stringResource(
          res = MR.strings.common_controls_status_poll_total_vote,
          it,
        )
      }
    }
    Row(
      modifier = Modifier.height(ButtonDefaults.MinHeight),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      CompositionLocalProvider(
        LocalContentAlpha.provides(ContentAlpha.disabled),
      ) {
        if (countText != null) {
          Text(text = countText)
        }
        Spacer(modifier = Modifier.width(PollDefaults.VoteTimeSpacing))
        if (data.poll.expired) {
          Text(text = stringResource(res = MR.strings.common_controls_status_poll_expired))
        } else {
          Text(text = data.poll.expiresAtString)
        }
      }
    }

    if (data.poll.canVote) {
      TextButton(
        onClick = {
          clickable.onVoteConfirm.invoke(data)
        },
        enabled = data.poll.anyVotes,
      ) {
        Text(text = stringResource(res = MR.strings.common_controls_status_actions_vote))
      }
    }
  }
}

object PollDefaults {
  val OptionSpacing = 8.dp
  val VoteSpacing = 8.dp
  val VoteInfoSpacing = 8.dp
  val VoteTimeSpacing = 8.dp
}

@Composable
fun PollOption(
  option: UiPollOption,
  poll: UiPoll,
  onVote: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val progress by animateFloatAsState(option.progress)
  val color = if (poll.expired) {
    MaterialTheme.colors.onBackground
  } else {
    MaterialTheme.colors.primary
  }
  CompositionLocalProvider(
    *if (poll.expired) {
      arrayOf(LocalContentAlpha provides ContentAlpha.medium)
    } else {
      emptyArray()
    },
  ) {
    Box(
      modifier = modifier
        .height(IntrinsicSize.Min).let {
          if (!poll.canVote) {
            it
          } else {
            it.clickable {
              onVote.invoke()
            }
          }
        }
        .clip(
          if (poll.multiple) {
            RoundedCornerShape(4.dp)
          } else {
            RoundedCornerShape(percent = 50)
          },
        ),
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            remember(poll.expired, color) {
              if (poll.expired) {
                color.copy(alpha = 0.0304f)
              } else {
                color.copy(alpha = 0.08f)
              }
            },
          ),
      )
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth(progress)
          .clip(
            if (poll.multiple) {
              RoundedCornerShape(4.dp)
            } else {
              RoundedCornerShape(percent = 50)
            },
          )
          .background(
            color.let {
              remember(poll.ownVotes, poll.expired) {
                // foreGroundAlpha =1 - (1 - wantedAlpha)/(1 - backgroundAlpha)
                if (option.voted) {
                  if (poll.expired) {
                    // wanted alpha is 0.75f * 0.38f, but still a little bit heaver, so down to 0.185f
                    it.copy(alpha = 0.185f)
                  } else {
                    // wanted alpha is 0.75f
                    it.copy(alpha = 0.62f)
                  }
                } else {
                  if (poll.expired) {
                    // wanted alpha is 0.2f * 0.38f
                    it.copy(alpha = 0.076f)
                  } else {
                    // wanted alpha is 0.2f
                    it.copy(alpha = 0.13f)
                  }
                }
              }
            },
          ),
      )
      Row(
        modifier = Modifier
          .wrapContentSize()
          .padding(PollOptionDefaults.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Box(
          modifier = Modifier.width(PollOptionDefaults.IconSize),
        ) {
          if (poll.canVote) {
            if (poll.multiple) {
              Checkbox(
                modifier = Modifier.size(LocalTextStyle.current.fontSize.value.dp),
                checked = option.voted,
                onCheckedChange = { onVote.invoke() },
              )
            } else {
              RadioButton(
                modifier = Modifier.size(LocalTextStyle.current.fontSize.value.dp),
                selected = option.voted,
                onClick = { onVote.invoke() },
              )
            }
          } else {
            androidx.compose.animation.AnimatedVisibility(
              visible = option.voted,
              enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
              exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut(),
            ) {
              Box(
                modifier = Modifier
                  .background(MaterialTheme.colors.surface, shape = CircleShape),
              ) {
                Icon(
                  modifier = Modifier
                    .padding(4.dp),
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                )
              }
            }
          }
        }
        Spacer(modifier = Modifier.width(PollOptionDefaults.IconSpacing))
        Text(
          modifier = Modifier.weight(1f),
          text = option.text,
          style = MaterialTheme.typography.body2,
        )
        Spacer(modifier = Modifier.width(PollOptionDefaults.IconSpacing))
        Text(
          text = option.progressText,
          style = MaterialTheme.typography.caption,
        )
      }
    }
  }
}

object PollOptionDefaults {
  val IconSize = 20.dp
  val ContentPadding = 6.dp
  val IconSpacing = 8.dp
}

@Composable
fun ColumnScope.UiStatusMediaContent(
  status: UiStatusWithMedia,
  clickable: TimelineClickable,
) {
  AnimatedVisibility(LocalDisplayPreferences.current.mediaPreview) {
    UiStatusMediaGridLayout(
      data = status,
      clickable = clickable,
    )
  }
  AnimatedVisibility(!LocalDisplayPreferences.current.mediaPreview) {
    MediaPreviewButton(
      onClick = {
        clickable.onMediaPreviewClicked.invoke(status)
      },
    )
  }
}

@Composable
fun UiStatusMediaGridLayout(
  data: UiStatusWithMedia,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .heightIn(max = UiStatusMediaGridLayoutDefaults.DefaultMaxHeight),
  ) {
    when (data.media.size) {
      1 -> {
        SingleMedia(
          status = data,
          clickable = clickable,
        )
      }

      3 -> {
        TripleMedia(
          status = data,
          clickable = clickable,
          modifier = modifier
            .aspectRatio(UiStatusMediaGridLayoutDefaults.DefaultAspectRatio),
        )
      }

      else -> {
        MultipleMedia(
          status = data,
          clickable = clickable,
          modifier = modifier
            .aspectRatio(UiStatusMediaGridLayoutDefaults.DefaultAspectRatio),
        )
      }
    }

    TwidereTheme(darkTheme = true) {
      AnimatedVisibility(
        modifier = Modifier
          .matchParentSize(),
        visible = data.status.data.sensitive,
      ) {
        Box(
          modifier = Modifier
            .clickable {
              clickable.onSensitiveClicked.invoke(data)
            },
          contentAlignment = Alignment.Center,
        ) {
          Box(
            modifier = Modifier
              .background(
                MaterialTheme.colors.surface.copy(alpha = 0.25f),
                shape = CircleShape,
              )
              .size(UiStatusMediaGridLayoutDefaults.Sensitive.BackgroundSize),
          ) {
            Icon(
              painter = painterResource(res = MR.files.ic_alert_triangle),
              contentDescription = null,
              tint = MaterialTheme.colors.onSurface,
              modifier = Modifier
                .size(UiStatusMediaGridLayoutDefaults.Sensitive.IconSize)
                .align(Alignment.Center),
            )
          }
        }
      }
      AnimatedVisibility(
        visible = !data.status.data.sensitive,
        enter = fadeIn(),
        exit = fadeOut(),
      ) {
        Box(
          modifier = Modifier
            .padding(UiStatusMediaGridLayoutDefaults.Mastodon.IconSpacing)
            .alpha(0.5f),
        ) {
          Box(
            modifier = Modifier
              .background(
                MaterialTheme.colors.surface,
                shape = MaterialTheme.shapes.small,
              )
              .align(Alignment.TopStart)
              .clickable {
                clickable.onSensitiveClicked.invoke(data)
              }
              .clip(MaterialTheme.shapes.small)
              .padding(UiStatusMediaGridLayoutDefaults.Icon.ContentPadding),
          ) {
            Icon(
              painter = painterResource(res = MR.files.ic_eye_off),
              contentDescription = null,
              tint = MaterialTheme.colors.onSurface,
            )
          }
        }
      }
    }
  }
}

@Composable
fun MultipleMedia(
  status: UiStatusWithMedia,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  GridLayout(
    modifier = modifier,
    spacing = UiStatusMediaGridLayoutDefaults.MediaSpacing,
  ) {
    status.media.forEach { media ->
      StatusMediaPreviewItem(
        media = media,
        onClick = {
          clickable.onMediaClicked.invoke(status, media)
        },
        sensitive = status.status.data.sensitive,
      )
    }
  }
}

@Composable
fun TripleMedia(
  status: UiStatusWithMedia,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  val firstMedia = remember { status.media[0] }
  val secondMedia = remember { status.media[1] }
  val thirdMedia = remember { status.media[2] }
  Row(
    modifier = modifier,
  ) {
    StatusMediaPreviewItem(
      media = firstMedia,
      onClick = {
        clickable.onMediaClicked.invoke(status, firstMedia)
      },
      modifier = Modifier
        .weight(1f),
    )

    Spacer(
      modifier = Modifier
        .width(UiStatusMediaGridLayoutDefaults.MediaSpacing),
    )

    Column(
      modifier = Modifier.weight(1f),
    ) {
      StatusMediaPreviewItem(
        media = secondMedia,
        modifier = Modifier
          .weight(1f)
          .fillMaxSize(),
        sensitive = status.status.data.sensitive,
        onClick = {
          clickable.onMediaClicked.invoke(status, secondMedia)
        },
      )
      Spacer(
        modifier = Modifier
          .height(UiStatusMediaGridLayoutDefaults.MediaSpacing),
      )
      StatusMediaPreviewItem(
        media = thirdMedia,
        modifier = Modifier
          .weight(1f)
          .fillMaxSize(),
        sensitive = status.status.data.sensitive,
        onClick = {
          clickable.onMediaClicked.invoke(status, thirdMedia)
        },
      )
    }
  }
}

object UiStatusMediaGridLayoutDefaults {
  val DefaultAspectRatio = 270f / 162f
  val DefaultMaxHeight = 400.dp
  val MediaSpacing = 8.dp

  object Mastodon {
    val IconSpacing = 8.dp
  }

  object Icon {
    val ContentPadding = 6.dp
  }

  object Sensitive {
    val BackgroundSize = 48.dp
    val IconSize = 30.dp
  }
}

@Composable
fun SingleMedia(
  status: UiStatusWithMedia,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  val media = remember { status.media.first() }
  StatusMediaPreviewItem(
    media = media,
    onClick = {
      clickable.onMediaClicked.invoke(status, media)
    },
    modifier = modifier.aspectRatio(media.aspectRatio),
    sensitive = status.status.data.sensitive,
  )
}

@Composable
fun MediaPreviewButton(
  onClick: () -> Unit,
) {
  CompositionLocalProvider(
    LocalContentAlpha provides ContentAlpha.medium,
  ) {
    Row(
      modifier = Modifier
        .background(
          LocalContentColor.current.copy(alpha = 0.04f),
          shape = MaterialTheme.shapes.small,
        )
        .clickable(
          onClick = {
            onClick.invoke()
          },
        )
        .clip(MaterialTheme.shapes.small)
        .padding(horizontal = 4.dp)
        .height(30.dp),
    ) {
      Icon(
        painter = painterResource(res = MR.files.ic_photo),
        contentDescription = stringResource(
          res = MR.strings.accessibility_common_status_media,
        ),
      )
      Spacer(modifier = Modifier.width(MediaPreviewButtonDefaults.IconSpacing))
      Text(text = stringResource(res = MR.strings.common_controls_status_media))
    }
  }
}

@Composable
fun ColumnScope.UiStatusCardContent(
  data: UiStatusWithCard,
  clickable: TimelineClickable,
) {
  AnimatedVisibility(
    LocalDisplayPreferences.current.urlPreview,
  ) {
    LinkPreview(
      modifier = Modifier
        .fillMaxWidth()
        .clickable {
          clickable.onLinkClicked.invoke(data.card.link)
        },
      link = data.card.displayLink ?: data.card.link,
      title = data.card.title,
      image = data.card.image,
      desc = data.card.description,
      maxLines = 5,
    )
  }
}

@Composable
fun UiStatusTimelineComponent(
  data: UiStatusTimeline,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
  extraContent: @Composable ColumnScope.() -> Unit = {},
) {
  when (data) {
    is UiMastodonStatus -> {
      UiMastodonStatusComponent(
        data = data,
        clickable = clickable,
        modifier = modifier,
        extraContent = extraContent,
      )
    }

    is UiTwitterStatus -> {
      UiTwitterStatusComponent(
        data = data,
        clickable = clickable,
        modifier = modifier,
        extraContent = extraContent,
      )
    }
  }
}

@Composable
fun UiRetweetStatusComponent(
  data: UiRetweetStatus,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
  extraContent: @Composable ColumnScope.() -> Unit = {},
) {
  Column(modifier = modifier) {
    RetweetHeader(
      name = data.status.status.data.user.displayName,
      openLink = clickable.onLinkClicked,
    )
    UiStatusWithExtraComponent(
      data = data.retweet,
      clickable = clickable,
      extraContent = extraContent,
    )
  }
}

@Composable
fun UiTwitterStatusComponent(
  data: UiTwitterStatus,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
  extraContent: @Composable ColumnScope.() -> Unit = {},
) {
  Column(
    modifier = Modifier
      .clickable {
        clickable.onStatusClicked.invoke(data)
      }
      .then(modifier),
  ) {
    Row {
      UserAvatar(
        user = data.data.user,
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
              data.data.user,
              onUserNameClicked = clickable.onLinkClicked,
            )
            Spacer(modifier = Modifier.width(6.dp))
            UserScreenName(data.data.user)
          }
          CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.disabled,
          ) {
            Text(data.data.humanizedTime)
          }
        }
        StatusText(
          data = data,
          clickable = clickable,
        )
        extraContent.invoke(this)
        UiStatusMetricsComponent(
          status = data,
          clickable = clickable,
        )
      }
    }
  }
}

object TimelineComponentDefaults {
  val AvatarSpacing = 10.dp
}

@Composable
fun UiMastodonStatusComponent(
  data: UiMastodonStatus,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
  extraContent: @Composable ColumnScope.() -> Unit = {},
) {
  Column(
    modifier = Modifier
      .clickable {
        clickable.onStatusClicked.invoke(data)
      }
      .then(modifier),
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
        user = data.data.user,
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
              data.data.user,
              onUserNameClicked = clickable.onLinkClicked,
            )
            Spacer(modifier = Modifier.width(6.dp))
            UserScreenName(data.data.user)
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
            Text(data.data.humanizedTime)
          }
        }
        if (data.spoilerText != null) {
          MastodonContentWithSpoiler(
            data = data,
            clickable = clickable,
          )
        } else {
          StatusText(
            data = data,
            clickable = clickable,
          )
        }
        extraContent.invoke(this)
        UiStatusMetricsComponent(
          status = data,
          clickable = clickable,
        )
      }
    }
  }
}

@Composable
fun StatusText(
  data: UiStatusTimeline,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  when (data) {
    is UiMastodonStatus -> MastodonText(
      data = data,
      clickable = clickable,
      token = data.data.parsedContent,
      modifier = modifier,
    )

    is UiTwitterStatus -> TwitterText(
      data = data.data,
      clickable = clickable,
      token = data.data.parsedContent,
      modifier = modifier,
    )
  }
}

@Composable
fun TwitterText(
  token: ImmutableList<Token>,
  data: UiStatusMetaData,
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
  )
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
    MetricsButton(
      onClick = {
        clickable.onReplyClicked.invoke(status)
      },
      icon = painterResource(res = MR.files.ic_corner_up_left),
      contentDescription = stringResource(res = MR.strings.accessibility_common_status_actions_reply),
      showNumbers = status.data.metrics.hasReplyCount,
      countString = status.data.metrics.humanizedReplyCount,
      modifier = Modifier.weight(1f),
    )
    Box(
      modifier = Modifier.weight(1f),
    ) {
      MetricsButton(
        onClick = {
          clickable.onRetweetMenuOpenRequest.invoke(status)
        },
        icon = painterResource(res = MR.files.ic_repeat),
        contentDescription = stringResource(res = MR.strings.accessibility_common_status_actions_retweet),
        showNumbers = status.data.metrics.hasRetweetCount,
        countString = status.data.metrics.humanizedRetweetCount,
        tint = if (status.data.metrics.retweeted) {
          MaterialTheme.colors.primary
        } else {
          LocalContentColor.current
        },
      )

      DropdownMenu(
        expanded = status.data.menu.retweetOpened,
        onDismissRequest = { clickable.onRetweetMenuCloseRequest.invoke(status) },
      ) {
        DropdownMenuItem(
          onClick = {
            if (status.data.metrics.retweeted) {
              clickable.onUnRetweetClicked.invoke(status)
            } else {
              clickable.onRetweetClicked.invoke(status)
            }
          },
        ) {
          Text(text = stringResource(res = MR.strings.common_controls_status_actions_retweet))
        }
        DropdownMenuItem(
          onClick = {
            clickable.onQuoteClicked.invoke(status)
          },
        ) {
          Text(
            text = stringResource(res = MR.strings.common_controls_status_actions_quote),
          )
        }
      }
    }

    MetricsButton(
      onClick = {
        clickable.onReplyClicked.invoke(status)
      },
      icon = painterResource(res = MR.files.ic_heart),
      contentDescription = stringResource(res = MR.strings.accessibility_common_status_actions_like),
      showNumbers = status.data.metrics.hasReplyCount,
      countString = status.data.metrics.humanizedReplyCount,
      modifier = Modifier.weight(1f),
      tint = if (status.data.metrics.liked) {
        Color.Red
      } else {
        LocalContentColor.current
      },
    )

    Box {
      IconButton(
        onClick = {
          clickable.onMoreMenuOpenRequest.invoke(status)
        },
      ) {
        Icon(
          imageVector = Icons.Default.MoreHoriz,
          tint = LocalContentColor.current,
          contentDescription = stringResource(res = MR.strings.accessibility_common_more),
        )
      }

      DropdownMenu(
        expanded = status.data.menu.moreOpened,
        onDismissRequest = { clickable.onMoreMenuCloseRequest.invoke(status) },
      ) {
        DropdownMenuItem(
          onClick = {
            clickable.onCopyTextClicked.invoke(status)
          },
        ) {
          Text(
            text = stringResource(res = MR.strings.common_controls_status_actions_copy_text),
          )
        }
        DropdownMenuItem(
          onClick = {
            clickable.onCopyLinkClicked.invoke(status)
          },
        ) {
          Text(
            text = stringResource(res = MR.strings.common_controls_status_actions_copy_link),
          )
        }
        DropdownMenuItem(
          onClick = {
            clickable.onShareLinkClicked.invoke(status)
          },
        ) {
          Text(
            text = stringResource(res = MR.strings.common_controls_status_actions_share_link),
          )
        }
        DropdownMenuItem(
          onClick = {
            clickable.onShareContentClicked.invoke(status)
          },
        ) {
          Text(
            text = stringResource(res = MR.strings.common_controls_status_actions_share_content),
          )
        }
        if (status.data.owned) {
          DropdownMenuItem(
            onClick = {
              clickable.onRemoveStatusClicked.invoke(status)
            },
          ) {
            Text(
              text = stringResource(res = MR.strings.common_controls_actions_remove),
              color = Color.Red,
            )
          }
        }
      }
    }
  }
}

@Composable
fun MetricsButton(
  onClick: () -> Unit,
  icon: Painter,
  showNumbers: Boolean,
  countString: String,
  modifier: Modifier = Modifier,
  tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
  contentDescription: String? = null,
) {
  Row(
    modifier = modifier
      .clickable {
        onClick.invoke()
      },
    verticalAlignment = Alignment.CenterVertically,
  ) {
    IconButton(
      onClick = {
        onClick.invoke()
      },
    ) {
      Icon(
        modifier = Modifier
          .size(MaterialTheme.typography.caption.fontSize.value.dp),
        painter = icon,
        contentDescription = contentDescription,
        tint = tint,
      )
    }
    if (showNumbers) {
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = countString,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.caption,
        color = tint,
      )
    }
  }
}

@Composable
fun MastodonText(
  token: ImmutableList<Token>,
  data: UiMastodonStatus,
  clickable: TimelineClickable,
  modifier: Modifier = Modifier,
) {
  TokenText(
    token = token,
    layoutDirection = data.data.contentDirection,
    onLinkClicked = clickable.onLinkClicked,
    modifier = modifier,
    linkResolver = {
      data.data.resolveLink(it)
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
    StatusText(
      data = data,
      clickable = clickable,
    )
    Row(
      modifier = Modifier
        .clickable {
          if (data.expanded) {
            clickable.onCollapseClicked(data)
          } else {
            clickable.onExpandClicked(data)
          }
        }
        .size(width = 46.dp, height = 20.dp)
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
      StatusText(
        data = data,
        clickable = clickable,
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
              data.data.user.displayName,
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
              data.data.user.displayName,
            ),
          )
        },
      )
    }

    MastodonNotificationType.Mention -> Unit
    MastodonNotificationType.Reblog -> {
      RetweetHeader(
        name = data.data.user.displayName,
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
              data.data.user.displayName,
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
            if (data.data.owned) {
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
              data.data.user.displayName,
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
    modifier = Modifier
      .background(MaterialTheme.colors.surface)
      .clickable { clickable.onGapClicked(data) }
      .fillMaxWidth()
      .then(modifier),
    contentAlignment = Alignment.Center,
  ) {
    if (data.loading) {
      CircularProgressIndicator()
    } else {
      Text(
        modifier = Modifier.padding(vertical = 12.dp),
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
  val onMediaPreviewClicked: (UiStatusWithMedia) -> Unit,
  val onMediaClicked: (UiStatusWithMedia, UiMedia) -> Unit,
  val onRetweetMenuOpenRequest: (UiStatusTimeline) -> Unit,
  val onRetweetMenuCloseRequest: (UiStatusTimeline) -> Unit,
  val onQuoteClicked: (UiStatusTimeline) -> Unit,
  val onRetweetClicked: (UiStatusTimeline) -> Unit,
  val onUnRetweetClicked: (UiStatusTimeline) -> Unit,
  val onLikeClicked: (UiStatusTimeline) -> Unit,
  val onUnlikeClicked: (UiStatusTimeline) -> Unit,
  val onGapClicked: (UiGap) -> Unit,
  val onAcceptFollowClicked: (UiFollowRequest) -> Unit,
  val onRejectFollowClicked: (UiFollowRequest) -> Unit,
  val onLinkClicked: (String) -> Unit,
  val onExpandClicked: (UiStatusTimeline) -> Unit,
  val onCollapseClicked: (UiStatusTimeline) -> Unit,
  val onReplyClicked: (UiStatusTimeline) -> Unit,
  val onMoreMenuOpenRequest: (UiStatusTimeline) -> Unit,
  val onMoreMenuCloseRequest: (UiStatusTimeline) -> Unit,
  val onCopyTextClicked: (UiStatusTimeline) -> Unit,
  val onCopyLinkClicked: (UiStatusTimeline) -> Unit,
  val onShareLinkClicked: (UiStatusTimeline) -> Unit,
  val onShareContentClicked: (UiStatusTimeline) -> Unit,
  val onRemoveStatusClicked: (UiStatusTimeline) -> Unit,
  val onVote: (UiStatusWithPoll, UiPollOption) -> Unit,
  val onVoteConfirm: (UiStatusWithPoll) -> Unit,
  val onSensitiveClicked: (UiStatusWithMedia) -> Unit,
) {
  // companion object {
  //   val Empty = TimelineClickable(
  //     onUserClicked = {},
  //     onStatusClicked = {},
  //     onMediaClicked = { _, _ -> },
  //     onRetweetClicked = {},
  //     onUnretweetClicked = {},
  //     onLikeClicked = {},
  //     onUnlikeClicked = {},
  //     onGapClicked = {},
  //     onAcceptFollowClicked = {},
  //     onRejectFollowClicked = {},
  //     onLinkClicked = {},
  //     onExpandClicked = {},
  //     onCollapseClicked = {},
  //     onReplyClicked = {},
  //     onRetweetMenuOpenRequest = {},
  //     onRetweetMenuCloseRequest = {},
  //     onQuoteClicked = {},
  //     onMoreMenuOpenRequest = {},
  //     onMoreMenuCloseRequest = {},
  //     onCopyTextClicked = {},
  //     onCopyLinkClicked = {},
  //     onShareLinkClicked = {},
  //     onShareContentClicked = {},
  //     onRemoveStatusClicked = {},
  //     onMediaPreviewClicked = {},
  //   )
  // }
}
