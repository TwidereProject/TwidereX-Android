/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.fragment

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRowForIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pages
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Topic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.node.Ref
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.twidere.twiderex.component.AppBar
import com.twidere.twiderex.component.AppBarNavigationButton
import com.twidere.twiderex.component.NetworkImage
import com.twidere.twiderex.component.StatusLineComponent
import com.twidere.twiderex.component.TextInput
import com.twidere.twiderex.component.TimelineStatusComponent
import com.twidere.twiderex.extensions.NavControllerAmbient
import com.twidere.twiderex.extensions.checkAllSelfPermissionsGranted
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.maxComposeTextLength
import com.twidere.twiderex.ui.composeImageSize
import com.twidere.twiderex.ui.profileImageSize
import com.twidere.twiderex.ui.standardPadding
import com.twidere.twiderex.viewmodel.ActiveAccountViewModel
import com.twidere.twiderex.viewmodel.ComposeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComposeFragment : JetFragment() {
    private val args by navArgs<ComposeFragmentArgs>()
    private val viewModel by viewModels<ComposeViewModel>()

    @OptIn(ExperimentalFoundationApi::class, ExperimentalFocus::class, ExperimentalLazyDsl::class)
    @Composable
    override fun onCompose() {
        val (textState, setTextState) = remember { mutableStateOf(TextFieldValue()) }
        val activeAccountViewModel = viewModel<ActiveAccountViewModel>()
        val images by viewModel.images.observeAsState(initial = emptyList())
        val account by activeAccountViewModel.account.observeAsState()
        val location by viewModel.location.observeAsState()
        val locationEnabled by viewModel.locationEnabled.observeAsState(initial = false)
        val navController = NavControllerAmbient.current
        val keyboardController = remember { Ref<SoftwareKeyboardController>() }
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = if (args.status == null) {
                0
            } else {
                1
            }
        )
        args.status?.also {
            if (listState.firstVisibleItemIndex == 0) {
                keyboardController.value?.hideSoftwareKeyboard()
            } else if (listState.firstVisibleItemIndex == 1) {
                keyboardController.value?.showSoftwareKeyboard()
            }
        }
        Scaffold(
            topBar = {
                AppBar(
                    title = {
                        args.status?.let {
                            Text(text = "Reply")
                        } ?: Text(text = "Compose")
                    },
                    navigationIcon = {
                        AppBarNavigationButton(icon = Icons.Default.Close)
                    },
                    actions = {
                        IconButton(
                            enabled = textState.text.isNotEmpty(),
                            onClick = {
                                viewModel.compose(textState.text)
                                navController.popBackStack()
                            }
                        ) {
                            Icon(asset = Icons.Default.Send)
                        }
                    }
                )
            }
        ) {
            Column {
                LazyColumn(
                    modifier = Modifier.weight(1F),
                    state = listState,
                ) {
                    args.status?.let { status ->
                        item {
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colors.surface.withElevation())
                            ) {
                                StatusLineComponent(lineDown = true) {
                                    TimelineStatusComponent(
                                        data = status,
                                        showActions = false,
                                    )
                                }
                            }
                        }
                    }
                    item {
                        StatusLineComponent(
                            lineUp = args.status != null,
                        ) {
                            Row(
                                modifier = Modifier.fillParentMaxSize()
                                    .padding(16.dp),
                            ) {
                                account?.let {
                                    NetworkImage(
                                        url = it.user.profileImage,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .width(profileImageSize)
                                            .height(profileImageSize)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Box(
                                    modifier = Modifier.weight(1F)
                                ) {
                                    TextInput(
                                        modifier = Modifier.align(Alignment.TopCenter),
                                        value = textState,
                                        onValueChange = { setTextState(it) },
                                        autoFocus = true,
                                        onTextInputStarted = {
                                            keyboardController.value = it
                                        },
                                        onClicked = {
                                            // TODO: scroll lazyColumn
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                if (images.any()) {
                    LazyRowForIndexed(
                        modifier = Modifier.padding(horizontal = standardPadding * 2),
                        items = images
                    ) { index, item ->
                        Box(
                            modifier = Modifier
                                .heightIn(max = composeImageSize)
                                .aspectRatio(1F)
                                .clip(RoundedCornerShape(8.dp)),
                        ) {
                            NetworkImage(url = item)
                        }
                        if (index != images.lastIndex) {
                            Spacer(modifier = Modifier.width(standardPadding))
                        }
                    }
                    Spacer(modifier = Modifier.height(standardPadding * 2))
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = standardPadding * 2)
                ) {
                    Box(
                        modifier = Modifier
                            .width(profileImageSize / 2)
                            .height(profileImageSize / 2),
                    ) {
                        CircularProgressIndicator(
                            progress = 1f,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                        )
                        CircularProgressIndicator(
                            progress = textState.text.length.toFloat() / maxComposeTextLength.toFloat(),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    if (locationEnabled) {
                        location?.let {
                            ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                                Row {
                                    Icon(asset = Icons.Default.Place)
                                    Text(text = "${it.latitude}, ${it.longitude}")
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(standardPadding * 2))
                Divider()
                Box {
                    Row {
                        IconButton(
                            onClick = {
                                openImagePicker()
                            }
                        ) {
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
                        IconButton(
                            onClick = {
                                if (locationEnabled) {
                                    disableLocation()
                                } else {
                                    getOrRequestLocation()
                                }
                            },
                        ) {
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

    private val openImagePicker =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            viewModel.putImages(it)
        }

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.values.all { it }) {
                findLocation()
            }
        }

    private fun getOrRequestLocation() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        context?.checkAllSelfPermissionsGranted(*permissions)?.let {
            if (it) {
                findLocation()
            } else {
                requestLocationPermission.launch(permissions)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun findLocation() {
        viewModel.trackingLocation()
    }

    private fun disableLocation() {
        viewModel.disableLocation()
    }

    private fun openImagePicker() {
        openImagePicker.launch("image/*")
    }
}
