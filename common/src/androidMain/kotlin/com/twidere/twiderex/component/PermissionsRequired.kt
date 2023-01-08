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
package com.twidere.twiderex.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.twidere.twiderex.component.foundation.AlertDialog

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TwiderexPermissionsRequired(
  permissions: List<String>,
  feature: String,
  onPermissionDenied: () -> Unit,
  content: @Composable () -> Unit
) {
  val context = LocalContext.current
  val multiplePermissionsState = rememberMultiplePermissionsState(permissions)
  val permissionNames = remember(permissions) { permissions.names(context) }
  PermissionsRequired(
    multiplePermissionsState = multiplePermissionsState,
    permissionsNotGrantedContent = {
      RequestDialog(
        onCancel = onPermissionDenied,
        onRequestPermission = { multiplePermissionsState.launchMultiplePermissionRequest() },
        permissions = permissionNames,
        feature = feature
      )
    },
    permissionsNotAvailableContent = {
      PermissionDeniedDialog(
        navigateToSettingsScreen = {
          context.startActivity(
            Intent(
              Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
              Uri.fromParts("package", context.packageName, null)
            )
          )
        },
        onCancel = onPermissionDenied,
        permissions = permissionNames
      )
    },
  ) {
    content()
  }
}

@ExperimentalPermissionsApi
@Composable
fun PermissionsRequired(
  multiplePermissionsState: MultiplePermissionsState,
  permissionsNotGrantedContent: @Composable (() -> Unit),
  permissionsNotAvailableContent: @Composable (() -> Unit),
  content: @Composable (() -> Unit),
) {
  when {
    multiplePermissionsState.allPermissionsGranted -> {
      content()
    }
    multiplePermissionsState.shouldShowRationale -> {
      permissionsNotAvailableContent()
    }
    else -> {
      permissionsNotGrantedContent()
    }
  }
}

private fun List<String>.names(context: Context): String {
  val pm = context.packageManager
  return this.map {
    pm.getPermissionInfo(it, 0).let { info ->
      info.group?.let { group ->
        pm.getPermissionGroupInfo(group, 0).loadLabel(pm)
      }.takeIf { it != "android.permission-group.UNDEFINED" } ?: info.loadLabel(pm)
    }
  }.joinToString { it }
}

@Composable
private fun RequestDialog(
  onCancel: () -> Unit,
  onRequestPermission: () -> Unit,
  permissions: String,
  feature: String,
) {
  AlertDialog(
    title = { Text("Allow $permissions access") },
    text = { Text("We need your permission to $feature") },
    confirmButton = {
      Button(
        onClick = onRequestPermission
      ) {
        Text("OK")
      }
    },
    dismissButton = {
      Button(
        onClick = onCancel
      ) {
        Text("Cancel")
      }
    },
    onDismissRequest = onCancel
  )
}

@Composable
private fun PermissionDeniedDialog(
  navigateToSettingsScreen: () -> Unit,
  onCancel: () -> Unit,
  permissions: String,
) {
  AlertDialog(
    title = { Text("Allow $permissions access") },
    text = { Text("Please, grant us access on the Settings screen") },
    confirmButton = {
      Button(
        onClick = navigateToSettingsScreen
      ) {
        Text("Open Settings")
      }
    },
    dismissButton = {
      Button(
        onClick = onCancel
      ) {
        Text("Cancel")
      }
    },
    onDismissRequest = onCancel
  )
}
