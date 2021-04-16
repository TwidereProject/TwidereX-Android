package com.twidere.twiderex.component.foundation

import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.utils.generateNotificationEvent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ErrorPlaceholder(
    throwable: Throwable,
    modifier: Modifier = Modifier,
) {
    val message = throwable.generateNotificationEvent()?.getMessage()
    ListItem(
        modifier = modifier,
        icon = {
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.medium
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                )
            }
        },
        text = {
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.medium
            ) {
                Text(
                    text = message
                        ?: stringResource(id = R.string.common_alerts_failed_to_load_title),
                )
            }
        }
    )
}