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
package com.twidere.twiderex.component.status

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twidere.services.mastodon.model.Option
import com.twidere.services.mastodon.model.Poll
import com.twidere.twiderex.R
import com.twidere.twiderex.action.LocalStatusActions
import com.twidere.twiderex.extensions.humanizedTimestamp
import com.twidere.twiderex.model.PlatformType
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.standardPadding
import kotlin.math.max

private val Poll.canVote: Boolean
    get() = voted != true && expiresAt?.time?.let { it > System.currentTimeMillis() } == true

@Composable
fun MastodonPoll(status: UiStatus) {
    val account = LocalActiveAccount.current ?: return
    if (status.platformType != PlatformType.Mastodon || status.mastodonExtra?.poll == null) {
        return
    }
    val voteState = remember {
        mutableStateListOf<Int>()
    }

    status.mastodonExtra.poll.options?.forEachIndexed { index, option ->
        MastodonPollOption(
            option,
            index,
            status.mastodonExtra.poll,
            voted = voteState.contains(index),
            onVote = {
                if (status.mastodonExtra.poll.multiple == true) {
                    if (voteState.contains(index)) {
                        voteState.remove(index)
                    } else {
                        voteState.add(index)
                    }
                } else {
                    if (voteState.isEmpty()) {
                        voteState.add(index)
                    } else {
                        voteState.clear()
                        voteState.add(index)
                    }
                }
            }
        )
        if (index != status.mastodonExtra.poll.options?.lastIndex) {
            Spacer(modifier = Modifier.height(standardPadding))
        }
    }

    if (status.mastodonExtra.poll.canVote) {
        Spacer(modifier = Modifier.height(standardPadding))
        val statusActions = LocalStatusActions.current
        TextButton(
            onClick = {
                statusActions.vote(status = status, account = account, votes = voteState)
            }
        ) {
            Text(text = stringResource(id = R.string.common_controls_status_actions_vote))
        }
    }

    Spacer(modifier = Modifier.height(standardPadding))
    Row {
        val countText = status.mastodonExtra.poll.votersCount?.let {
            if (it > 1) {
                stringResource(
                    id = R.string.common_controls_status_poll_total_people,
                    it,
                )
            } else {
                stringResource(
                    id = R.string.common_controls_status_poll_total_person,
                    it,
                )
            }
        } ?: status.mastodonExtra.poll.votesCount?.let {
            if (it > 1) {
                stringResource(
                    id = R.string.common_controls_status_poll_total_votes,
                    it,
                )
            } else {
                stringResource(
                    id = R.string.common_controls_status_poll_total_vote,
                    it,
                )
            }
        }
        if (countText != null) {
            Text(text = countText)
        }
        Spacer(modifier = Modifier.width(standardPadding))
        if (status.mastodonExtra.poll.expired == true) {
            Text(text = stringResource(id = R.string.common_controls_status_poll_expired))
        } else {
            Text(text = status.mastodonExtra.poll.expiresAt?.time?.humanizedTimestamp() ?: "")
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MastodonPollOption(
    option: Option,
    index: Int,
    poll: Poll,
    voted: Boolean,
    onVote: (voted: Boolean) -> Unit = {},
) {
    val size = LocalTextStyle.current.fontSize.value.dp + standardPadding * 3
    val transition = updateTransition(targetState = option.votesCount)
    val progress by transition.animateFloat {
        (option.votesCount ?: 0).toFloat() / max((poll.votesCount ?: 0), 1).toFloat()
    }
    val color = if (poll.expired == true) {
        MaterialTheme.colors.onBackground
    } else {
        MaterialTheme.colors.primary
    }
    Box(
        modifier = Modifier
            .clip(
                if (poll.multiple == true) {
                    RoundedCornerShape(4.dp)
                } else {
                    RoundedCornerShape(percent = 50)
                }
            ).let {
                if (!poll.canVote) {
                    it
                } else {
                    it.clickable {
                        onVote.invoke(!voted)
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .height(size)
                .fillMaxWidth()
                .background(color.copy(alpha = 0.0304f)),
        )
        Box(
            modifier = Modifier
                .height(size)
                .fillMaxWidth(progress)
                .clip(
                    if (poll.multiple == true) {
                        RoundedCornerShape(4.dp)
                    } else {
                        RoundedCornerShape(percent = 50)
                    }
                )
                .background(
                    color.let {
                        if (poll.ownVotes?.contains(index) == true) {
                            it.copy(alpha = 0.285f)
                        } else {
                            it.copy(alpha = 0.076f)
                        }
                    }
                ),
        )
        Row(
            modifier = Modifier
                .height(size),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(standardPadding))
            Box(
                modifier = Modifier.width(LocalTextStyle.current.fontSize.value.dp + standardPadding)
            ) {
                if (poll.canVote) {
                    if (poll.multiple == true) {
                        Checkbox(checked = voted, onCheckedChange = { onVote.invoke(it) })
                    } else {
                        RadioButton(selected = voted, onClick = { onVote.invoke(!voted) })
                    }
                } else {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = poll.ownVotes?.contains(
                            index
                        ) == true,
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
            Spacer(modifier = Modifier.width(standardPadding))
            Text(
                modifier = Modifier.weight(1f),
                text = option.title ?: "",
            )
            Text(text = String.format("%.0f%%", progress * 100))
            Spacer(modifier = Modifier.width(standardPadding))
        }
    }
}
