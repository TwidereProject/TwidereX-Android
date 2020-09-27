package com.twidere.twiderex.fragment

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.NetworkImage
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.viewmodel.twitter.ActiveAccountViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComposeFragment : JetFragment() {

    @OptIn(ExperimentalFoundationApi::class, ExperimentalFocus::class)
    @Composable
    override fun onCompose() {
        val textState = remember { mutableStateOf(TextFieldValue()) }
        val activeAccountViewModel = viewModel<ActiveAccountViewModel>()

        val focusRequester = FocusRequester()
        onActive {
            focusRequester.requestFocus()
        }
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
                Row(
                    modifier = Modifier.weight(1F)
                        .padding(16.dp)
                ) {
                    Column {
                        NetworkImage(
                            url = activeAccountViewModel.account.user.profileImage,
                            modifier = Modifier
                                .clip(CircleShape)
                                .width(profileImageSize)
                                .height(profileImageSize)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Stack(
                            modifier = Modifier
                                .width(profileImageSize / 2)
                                .height(profileImageSize / 2),
                        ) {
                            CircularProgressIndicator(
                                progress = 1f,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                            )
                            CircularProgressIndicator(
                                progress = textState.value.text.length.toFloat() / 1000f,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier.weight(1F)
                    ) {
                        BaseTextField(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Top)
                                .focusRequester(focusRequester),
                            value = textState.value,
                            onValueChange = { textState.value = it },
                        )
                    }
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