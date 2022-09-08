/*
 *  Twidere X
 *
 *  Copyright (C) TwidereProject and Contributors
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
package com.twidere.twiderex.scenes

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import moe.tlaster.precompose.navigation.Navigator
import com.twidere.twiderex.navigation.Root
import io.github.seiko.precompose.annotation.NavGraphDestination
import io.github.seiko.precompose.annotation.Path
import io.github.seiko.precompose.annotation.Query

@NavGraphDestination(
  route = Root.Media.Status.route,
)
@Composable
expect fun PlatformStatusMediaScene(
  @Path("statusKey") statusKey: String,
  @Query("selectedIndex") selectedIndex: Int?,
  navigator: Navigator,
)


@NavGraphDestination(
  route = Root.Media.Raw.route,
  functionName = "dialog",
)
@Composable
expect fun PlatformRawMediaScene(
  @Path("url") url: String,
  @Path("type") type: String,
  navigator: Navigator,
)


@NavGraphDestination(
  route = Root.Media.Pure.route,
)
@Composable
expect fun PlatformPureMediaScene(
  @Path("belongToKey") belongToKey: String,
  @Query("selectedIndex") selectedIndex: Int?,
  navigator: Navigator,
)

@NavGraphDestination(
  route =  Root.SignIn.Web.Twitter.route,
)
@Composable
expect fun PlatformScene(
  @Path("target") target: String,
  navigator: Navigator,
)

@Composable
expect fun StatusMediaSceneLayout(
  backgroundColor: Color,
  contentColor: Color,
  closeButton: @Composable () -> Unit,
  bottomView: @Composable () -> Unit,
  mediaView: @Composable () -> Unit,
  backgroundView: @Composable () -> Unit,
)
