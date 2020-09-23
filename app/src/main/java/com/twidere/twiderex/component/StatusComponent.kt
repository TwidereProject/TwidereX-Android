package com.twidere.twiderex.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Reply
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.db.model.DbStatusWithMedia
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.ui.buttonContentColor
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding

@Composable
fun ExpandedStatusComponent(
    data: DbTimelineWithStatus,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {})
            .padding(standardPadding),
    ) {
        val status = (data.retweet ?: data.status)
        if (data.retweet != null) {
            Row {
                Box(
                    modifier = Modifier
                        .width(profileImageSize),
                    gravity = ContentGravity.CenterEnd,
                ) {
                    Icon(asset = Icons.Default.Reply, tint = buttonContentColor)
                }
                Spacer(modifier = Modifier.width(standardPadding))
                Text(
                    text = data.status.status.user.name + "retweet this tweet",
                    color = buttonContentColor
                )
            }
            Spacer(modifier = Modifier.height(standardPadding))
        }
        StatusComponent(
            status = status,
            quote = data.quote,
            showActions = true,
        )

        if (!status.status.placeString.isNullOrEmpty()) {
            Row {
                Icon(asset = Icons.Default.Place)
                Text(text = status.status.placeString)
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
                    StatusComponent(
                        status = quote,
                        showActions = false,
                        modifier = Modifier
                            .clickable(onClick = {})
                            .padding(standardPadding),
                    )
                }
            }
        }
    }
}