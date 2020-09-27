package com.twidere.twiderex.fragment

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.fragment.navArgs
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.ExpandedStatusComponent

class StatusFragment : JetFragment() {

    private val args by navArgs<StatusFragmentArgs>()

    @Composable
    override fun onCompose() {
        Scaffold(
            topBar = {
                AppBar(
                    title = {
                        Text(text = "Tweet")
                    },
                    navigationIcon = {
                        AppBarNavigationButton()
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

