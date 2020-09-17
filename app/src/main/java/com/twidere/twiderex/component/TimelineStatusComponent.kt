package com.twidere.twiderex.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ColumnScope.weight
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
import com.twidere.twiderex.db.mapper.extraMedia
import com.twidere.twiderex.db.model.DbStatus
import com.twidere.twiderex.db.model.DbTimelineWithStatus
import com.twidere.twiderex.extensions.humanized
import com.twidere.twiderex.model.MediaData

val standardPadding = 8.dp
val profileImageSize = 44.dp

@Composable
fun TimelineStatusComponent(
    data: DbTimelineWithStatus,
    showActions: Boolean = true,
) {
    val status = data.retweet ?: data.status
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(standardPadding),
    ) {
        if (data.retweet != null) {
            Row {
                Box(
                    modifier = Modifier
                        .width(profileImageSize),
                    gravity = ContentGravity.CenterEnd,
                ) {
                    Icon(asset = Icons.Default.Reply, tint = buttonContentColor())
                }
                Spacer(modifier = Modifier.width(standardPadding))
                Text(
                    text = data.status.user.name + "retweet this tweet",
                    color = buttonContentColor()
                )
            }
        }
        StatusComponent(
            status = status,
            quote = data.quote,
            showActions = showActions,
        )
    }
}

@Composable
fun StatusComponent(
    status: DbStatus,
    quote: DbStatus? = null,
    showActions: Boolean = true,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            GlideImage(
                model = status.user.profileImage,
                modifier = Modifier
                    .clip(CircleShape)
                    .width(profileImageSize)
                    .height(profileImageSize)
            )
            Spacer(modifier = Modifier.width(standardPadding))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = status.user.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = status.user.screenName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = EmphasisAmbient.current.medium.applyEmphasis(
                                contentColor()
                            ),
                        )
                    }
                    Row {
                        Text(text = status.timestamp.toString())
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
                Text(text = status.text)

                if (status.hasMedia) {
                    val media = status.extraMedia()
                    if (media != null) {
//                        Spacer(modifier = Modifier.height(standardPadding))
//                        StatusMediaComponent(media = media)
                    }
                }

                if (!status.placeString.isNullOrEmpty()) {
                    Row {
                        Icon(asset = Icons.Default.Place)
                        Text(text = status.placeString)
                    }
                }

                if (quote != null) {
                    Box(
                        modifier = Modifier
                            .border(
                                1.dp,
                                contentColor().copy(alpha = 0.12f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(standardPadding)
                    ) {
                        StatusComponent(status = quote, showActions = false)
                    }
                }
                if (showActions) {
                    Spacer(modifier = Modifier.height(standardPadding))
                    Row {
                        StatusActionButton(
                            icon = Icons.Default.Reply,
                            count = status.replyCount,
                            onClick = {},
                        )
                        StatusActionButton(
                            icon = Icons.Default.Comment,
                            count = status.retweetCount,
                            onClick = {},
                        )
                        StatusActionButton(
                            icon = Icons.Default.Favorite,
                            count = status.likeCount,
                            onClick = {},
                        )
                        TextButton(
                            onClick = {},
                            contentColor = buttonContentColor(),
                        ) {
                            Icon(
                                asset = Icons.Default.Share,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusMediaComponent(
    media: List<MediaData>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(270f / 162f)
            .clip(RoundedCornerShape(8.dp))
    ) {
        when (media.size) {
            1 -> {
                media.firstOrNull()?.previewUrl?.let {
                    GlideImage(model = it)
                }
            }
//            2 -> {
//                Row {
//                    media.forEach {
//                        it.previewUrl?.let {
//                            GlideImage(model = it, modifier = Modifier.weight(1f))
//                        }
//                    }
//                }
//            }
//            3 -> {
//                Row {
//                    media.firstOrNull()?.previewUrl?.let {
//                        GlideImage(model = it, modifier = Modifier.weight(1f))
//                    }
//                    Column {
//                        media.drop(1).forEach {
//                            it.previewUrl?.let {
//                                GlideImage(model = it, modifier = Modifier.weight(1f))
//                            }
//                        }
//                    }
//                }
//            }
//            4 -> {
//                Row {
//                    Column {
//                        media.dropLast(2).forEach {
//                            it.previewUrl?.let {
//                                GlideImage(model = it, modifier = Modifier.weight(1f))
//                            }
//                        }
//                    }
//                    Column {
//                        media.drop(2).forEach {
//                            it.previewUrl?.let {
//                                GlideImage(model = it, modifier = Modifier.weight(1f))
//                            }
//                        }
//                    }
//                }
//            }
        }
    }
}

@Composable
fun StatusActionButton(
    modifier: Modifier = Modifier.weight(1f),
    icon: VectorAsset,
    count: Long,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        contentColor = buttonContentColor(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            Icon(asset = icon)
            if (count > 0) {
                Box(modifier = Modifier.width(4.dp))
                Text(text = count.toString())
            }
        }
    }
}

@Composable
private fun buttonContentColor(): Color = EmphasisAmbient.current.medium.applyEmphasis(
    contentColor()
)