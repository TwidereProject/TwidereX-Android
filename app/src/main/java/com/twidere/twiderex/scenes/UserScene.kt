package com.twidere.twiderex.scenes

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.annotations.IncomingComposeUpdate
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.UserComponent
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.ui.UiUser

@Composable
fun UserScene(name: String) {
    //TODO Load UiUser
}

@OptIn(IncomingComposeUpdate::class)
@Composable
fun UserScene(user: UiUser) {
    UserComponent(data = user) {
        AppBar(
            backgroundColor = MaterialTheme.colors.surface.withElevation(),
            navigationIcon = {
                AppBarNavigationButton()
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(asset = Icons.Default.Mail)
                }
                IconButton(onClick = {}) {
                    Icon(asset = Icons.Default.MoreVert)
                }
            },
            elevation = 0.dp,
        )
    }
}
