package com.twidere.twiderex.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope.weight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.R
import com.twidere.twiderex.db.model.DbStatusWithMedia
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.extensions.AmbientNavController
import com.twidere.twiderex.extensions.humanizedTimestamp
import com.twidere.twiderex.fragment.StatusFragmentArgs
import com.twidere.twiderex.ui.buttonContentColor
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding

@Composable
fun TimelineStatusComponent(
    data: DbTimelineWithStatus,
) {
    Column {
        val status = (data.retweet ?: data.status)
        val navController = AmbientNavController.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    navController.navigate(
                        R.id.status_fragment, StatusFragmentArgs(
                            status = data.status,
                            quote = data.quote,
                            retweet = data.retweet
                        ).toBundle()
                    )
                })
                .padding(
                    start = standardPadding * 2,
                    top = standardPadding * 2,
                    end = standardPadding * 2
                ),
        ) {
            if (data.retweet != null) {
                RetweetHeader(data = data.status)
                Spacer(modifier = Modifier.height(standardPadding))
            }
            StatusComponent(
                status = status,
                quote = data.quote,
                showActions = true,
            )
            Spacer(modifier = Modifier.height(standardPadding))
            Row {
                Spacer(modifier = Modifier.width(profileImageSize))
                StatusActionButton(
                    icon = Icons.Default.Reply,
                    count = status.status.replyCount,
                    onClick = {},
                )
                StatusActionButton(
                    icon = Icons.Default.Comment,
                    count = status.status.retweetCount,
                    onClick = {},
                )
                StatusActionButton(
                    icon = Icons.Default.Favorite,
                    count = status.status.likeCount,
                    onClick = {},
                )
                TextButton(
                    onClick = {},
                    contentColor = buttonContentColor,
                ) {
                    Icon(
                        asset = Icons.Default.Share,
                    )
                }
            }
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
    Row(modifier = modifier) {
        NetworkImage(
            url = status.status.user.profileImage,
            modifier = Modifier
                .clip(CircleShape)
                .width(profileImageSize)
                .height(profileImageSize)
        )
        Spacer(modifier = Modifier.width(standardPadding))
        Column {
            Row {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = status.status.user.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0XFF4C9EEB)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "@${status.status.user.screenName}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = EmphasisAmbient.current.medium.applyEmphasis(
                            contentColor()
                        ),
                    )
                }
                Row {
                    Text(text = status.status.timestamp.humanizedTimestamp())
                    if (showActions) {
                        Icon(
                            asset = Icons.Default.ArrowDropDown,
                            modifier = Modifier
                                .clickable(
                                    onClick = {},
                                ),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = status.status.text)

            if (status.media.any()) {
                Spacer(modifier = Modifier.height(standardPadding))
                StatusMediaComponent(
                    status = status,
                )
            }

            if (!status.status.placeString.isNullOrEmpty()) {
                Row {
                    Icon(asset = Icons.Default.Place)
                    Text(text = status.status.placeString)
                }
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
private fun StatusActionButton(
    modifier: Modifier = Modifier.weight(1f),
    icon: VectorAsset,
    count: Long,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
    ) {
        TextButton(
            onClick = onClick,
            contentColor = buttonContentColor,
        ) {
            Icon(asset = icon)
            if (count > 0) {
                Box(modifier = Modifier.width(4.dp))
                Text(text = count.toString())
            }
        }
    }
}
