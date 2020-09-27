package com.twidere.twiderex.fragment

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope.align
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton

class ComposeFragment : JetFragment() {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun onCompose() {
        val textState = remember { mutableStateOf(TextFieldValue()) }
        Scaffold(
            topBar = {
                AppBar(
                    title = {
                        Text(text = "Compose")
                    },
                    navigationIcon = {
                        AppBarNavigationButton(icon = Icons.Default.Close)
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.Send)
                        }
                    }
                )
            }
        ) {
            Column {
                Box(
                    modifier = Modifier.weight(1F)
                ) {
                    BaseTextField(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Top)
                            .align(Alignment.Start)
                            .background(color = Color.Transparent),
                        value = textState.value,
                        onValueChange = { textState.value = it }
                    )
                }
                Divider()
                Box {
                    Row {
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.Camera)
                        }
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.Gif)
                        }
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.AlternateEmail)
                        }
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.Topic)
                        }
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.MyLocation)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.Pages)
                        }
                    }
                }
            }
        }
    }
}