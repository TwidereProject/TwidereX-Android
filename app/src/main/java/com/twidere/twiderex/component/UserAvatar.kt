package com.twidere.twiderex.component

import androidx.compose.foundation.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.Dp
import com.twidere.twiderex.R
import com.twidere.twiderex.extensions.AmbientNavController
import com.twidere.twiderex.fragment.UserFragmentArgs
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.ui.profileImageSize

@Composable
fun UserAvatar(
    user: UiUser,
    size: Dp = profileImageSize
) {
    val navController = AmbientNavController.current
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clipToBounds()
    ) {
        NetworkImage(
            url = user.profileImage,
            modifier = Modifier
                .clickable(onClick = {
                    navController.navigate(R.id.user_fragment, UserFragmentArgs(user).toBundle())
                })
                .width(size)
                .height(size)
        )
    }
}