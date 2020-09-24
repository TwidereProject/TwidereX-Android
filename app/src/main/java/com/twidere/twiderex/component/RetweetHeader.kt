package com.twidere.twiderex.component

import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reply
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.twidere.twiderex.db.model.DbStatusWithMedia
import com.twidere.twiderex.ui.buttonContentColor
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding

@Composable
fun RetweetHeader(
    data: DbStatusWithMedia,
) {
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
            text = data.status.user.name + "retweet this tweet",
            color = buttonContentColor
        )
    }
}