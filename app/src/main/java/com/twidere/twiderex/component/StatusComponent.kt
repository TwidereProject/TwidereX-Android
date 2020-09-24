package com.twidere.twiderex.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.twidere.twiderex.db.model.DbStatusWithMedia
import com.twidere.twiderex.extensions.AmbientNavController
import com.twidere.twiderex.extensions.humanizedTimestamp
import com.twidere.twiderex.fragment.StatusFragmentArgs
import com.twidere.twiderex.ui.buttonContentColor
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding

@Composable
fun ExpandedStatusComponent(
    status: DbStatusWithMedia,
    retweet: DbStatusWithMedia?,
    quote: DbStatusWithMedia?,
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
        val data = (retweet ?: status)
        if (retweet != null) {
            RetweetHeader(data = status)
            Spacer(modifier = Modifier.height(standardPadding))
        }
        StatusComponent(
            status = data,
            quote = quote,
            showActions = true,
        )

        if (!data.status.placeString.isNullOrEmpty()) {
            Row {
                Icon(asset = Icons.Default.Place)
                Text(text = data.status.placeString)
            }
        }

        Spacer(modifier = Modifier.height(standardPadding))

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = status.status.timestamp.humanizedTimestamp(),
                color = buttonContentColor
            )
        }

        Spacer(modifier = Modifier.height(standardPadding))

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            if (status.status.replyCount > 0) {
                StatusStatistics(count = status.status.replyCount.toString(), text = "reply")
                Spacer(modifier = Modifier.width(standardPadding * 2))
            }
            if (status.status.retweetCount > 0) {
                StatusStatistics(
                    count = status.status.retweetCount.toString(),
                    text = "retweets"
                )
                Spacer(modifier = Modifier.width(standardPadding * 2))
            }
            if (status.status.likeCount > 0) {
                StatusStatistics(count = status.status.likeCount.toString(), text = "likes")
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
    status: DbStatusWithMedia,
    modifier: Modifier = Modifier,
    quote: DbStatusWithMedia? = null,
    showActions: Boolean = true,
) {
    Box(modifier = modifier) {
        Column {
            Row {
                NetworkImage(
                    url = status.status.user.profileImage,
                    modifier = Modifier
                        .clip(CircleShape)
                        .width(profileImageSize)
                        .height(profileImageSize)
                )
                Spacer(modifier = Modifier.width(standardPadding))
                Column {
                    Text(
                        text = status.status.user.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0XFF4C9EEB)
                    )
                    Text(
                        text = "@${status.status.user.screenName}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = EmphasisAmbient.current.medium.applyEmphasis(
                            contentColor()
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(standardPadding))

            Text(text = status.status.text)

            if (status.media.any()) {
                Spacer(modifier = Modifier.height(standardPadding))
                StatusMediaComponent(
                    status = status,
                )
            }

            if (quote != null) {
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
                    val navController = AmbientNavController.current
                    StatusComponent(
                        status = quote,
                        showActions = false,
                        modifier = Modifier
                            .clickable(onClick = {
                                navController.navigate(
                                    R.id.status_fragment, StatusFragmentArgs(
                                        status = quote,
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
        Text(text = text, color = buttonContentColor)
    }
}