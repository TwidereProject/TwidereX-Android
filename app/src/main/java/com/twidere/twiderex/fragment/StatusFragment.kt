package com.twidere.twiderex.fragment

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.navArgs
import com.twidere.twiderex.component.ExpandedStatusComponent
import com.twidere.twiderex.extensions.AmbientNavController

class StatusFragment : ComposeFragment() {

    val args by navArgs<StatusFragmentArgs>()

    @Composable
    override fun onCompose() {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.surface,
                    title = {
                        Text(text = "Tweet")
                    },
                    navigationIcon = {
                        val navController = AmbientNavController.current
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(asset = Icons.Default.ArrowBack)
                        }
                    }
                )
            }
        ) {
            ScrollableColumn {
                ExpandedStatusComponent(
                    status = args.status,
                    retweet = args.retweet,
                    quote = args.quote,
                )
            }
        }
    }
}

