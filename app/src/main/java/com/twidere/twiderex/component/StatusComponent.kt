package com.twidere.twiderex.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.extensions.NavControllerAmbient
import com.twidere.twiderex.extensions.humanizedTimestamp
import com.twidere.twiderex.fragment.StatusFragmentArgs
import com.twidere.twiderex.fragment.UserFragmentArgs
import com.twidere.twiderex.model.ui.UiStatus
import com.twidere.twiderex.ui.mediumEmphasisContentContentColor
import com.twidere.twiderex.ui.standardPadding

@Composable
fun ExpandedStatusComponent(
    status: UiStatus,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {})
            .padding(
                start = standardPadding * 2,
                top = standardPadding * 2,
                end = standardPadding * 2
            ),
    ) {
        val data = (status.retweet ?: status)
        if (status.retweet != null) {
            RetweetHeader(data = status)
            Spacer(modifier = Modifier.height(standardPadding))
        }
        StatusComponent(
            status = data,
            showActions = true,
        )

        if (!data.placeString.isNullOrEmpty()) {
            Row {
                Icon(asset = Icons.Default.Place)
                Text(text = data.placeString)
            }
        }

        Spacer(modifier = Modifier.height(standardPadding))

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = status.timestamp.humanizedTimestamp(),
                color = mediumEmphasisContentContentColor
            )
        }

        Spacer(modifier = Modifier.height(standardPadding))

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            if (status.replyCount > 0) {
                StatusStatistics(count = status.replyCount.toString(), text = "reply")
                Spacer(modifier = Modifier.width(standardPadding * 2))
            }
            if (status.retweetCount > 0) {
                StatusStatistics(
                    count = status.retweetCount.toString(),
                    text = "retweets"
                )
                Spacer(modifier = Modifier.width(standardPadding * 2))
            }
            if (status.likeCount > 0) {
                StatusStatistics(count = status.likeCount.toString(), text = "likes")
            }
        }

        Spacer(modifier = Modifier.height(standardPadding))

        Row {
            Spacer(modifier = Modifier.weight(1f))
            ActionIconButton(onClick = {}) {
                Icon(asset = Icons.Default.Reply)
            }
            ActionIconButton(onClick = {}) {
                Icon(asset = Icons.Default.Comment)
            }
            ActionIconButton(onClick = {}) {
                Icon(asset = Icons.Default.Favorite)
            }
            ActionIconButton(onClick = {}) {
                Icon(asset = Icons.Default.Share)
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatusComponent(
    status: UiStatus,
    modifier: Modifier = Modifier,
    showActions: Boolean = true,
) {
    Box(modifier = modifier) {
        Column {
            val navController = NavControllerAmbient.current
            Row(
                modifier = Modifier.clickable(onClick = {
                    navController.navigate(R.id.user_fragment, UserFragmentArgs(status.user).toBundle())
                })
            ) {
                UserAvatar(user = status.user)
                Spacer(modifier = Modifier.width(standardPadding))
                Column {
                    Text(
                        text = status.user.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0XFF4C9EEB)
                    )
                    Text(
                        text = "@${status.user.screenName}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = EmphasisAmbient.current.medium.applyEmphasis(
                            contentColor()
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(standardPadding))

            Text(text = status.text)

            if (status.media.any()) {
                Spacer(modifier = Modifier.height(standardPadding))
                StatusMediaComponent(
                    status = status,
                )
            }

            if (status.quote != null) {
                Spacer(modifier = Modifier.height(standardPadding))
                Box(
                    modifier = Modifier
                        .border(
                            1.dp,
                            contentColor().copy(alpha = 0.12f),
                            RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    val navController = NavControllerAmbient.current
                    StatusComponent(
                        status = status.quote,
                        showActions = false,
                        modifier = Modifier
                            .clickable(onClick = {
                                navController.navigate(
                                    R.id.status_fragment, StatusFragmentArgs(
                                        status = status.quote,
                                        quote = null,
                                        retweet = null
                                    ).toBundle()
                                )
                            })
                            .padding(standardPadding),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusStatistics(
    count: String,
    text: String,
) {
    Row {
        Text(text = count)
        Spacer(modifier = Modifier.width(standardPadding))
        Text(text = text, color = mediumEmphasisContentContentColor)
    }
}