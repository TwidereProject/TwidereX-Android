/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twidere.twiderex.action.LocalStatusActions
import com.twidere.twiderex.action.StatusActions
import com.twidere.twiderex.component.foundation.VideoPlayer
import com.twidere.twiderex.component.foundation.rememberVideoPlayerState
import com.twidere.twiderex.compose.LocalResLoader
import com.twidere.twiderex.di.ext.get
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.kmp.LocalRemoteNavigator
import com.twidere.twiderex.navigation.Router
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.utils.LocalPlatformResolver
import com.twidere.twiderex.utils.video.CustomVideoControl
import moe.tlaster.precompose.navigation.NavController

@Composable
fun App(navController: NavController = NavController()) {
    val accountViewModel =
        com.twidere.twiderex.di.ext.getViewModel<com.twidere.twiderex.viewmodel.ActiveAccountViewModel>()
    val account by accountViewModel.account.observeAsState(null)
    CompositionLocalProvider(
        LocalResLoader provides get(),
        LocalRemoteNavigator provides get(),
        LocalActiveAccount provides account,
        LocalActiveAccountViewModel provides accountViewModel,
        LocalStatusActions provides get<StatusActions>(),
        LocalPlatformResolver provides get(),
        LocalRemoteNavigator provides get(),
    ) {
        Router(
            navController = navController
        )
        // VideoPlayerDemo(navController)
    }
}

@Composable
fun VideoPlayerDemo(navController: NavController) {
    // val showVideo = remember { mutableStateOf(false) }
    // if (showVideo.value) {
    navController.toString()
    val state =
        rememberVideoPlayerState(url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
    Column(modifier = Modifier.fillMaxSize()) {
        VideoPlayer(
            modifier = Modifier.height(500.dp).fillMaxWidth(),
            videoState = state
        )
        CustomVideoControl(
            state = state,
            modifier = Modifier.fillMaxWidth()
        )
        // Button(onClick = {
        //     showVideo.value = false
        // }) {
        //     Text("release")
        // }
    }
    // }else {
    //     Button(onClick = {
    //         showVideo.value = true
    //     }){
    //
    //     }
    // }
}
