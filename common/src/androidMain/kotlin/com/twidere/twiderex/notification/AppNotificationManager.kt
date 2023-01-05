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
package com.twidere.twiderex.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.Html
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.seiko.imageloader.ImageLoaderBuilder
import com.seiko.imageloader.request.ComposeImageResult
import com.seiko.imageloader.request.ImageRequestBuilder
import com.twidere.common.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

actual class AppNotificationManager(
  private val context: Context,
  private val notificationManagerCompat: NotificationManagerCompat
) {
  private val scope = CoroutineScope(Dispatchers.IO)

  actual fun notify(notificationId: Int, appNotification: AppNotification) {
    if (!notificationManagerCompat.areNotificationsEnabled()) {
      return
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ActivityCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        return
      }
    }
    scope.launch {
      val builder = NotificationCompat.Builder(
        context,
        appNotification.channelId
      ).setSmallIcon(R.drawable.ic_notification)
        .setCategory(NotificationCompat.CATEGORY_SOCIAL)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .setContentTitle(appNotification.title)
        .setOngoing(appNotification.ongoing)
        .setSilent(appNotification.silent)
        .setProgress(
          appNotification.progressMax,
          appNotification.progress,
          appNotification.progressIndeterminate
        )

      appNotification.content?.let {
        val text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          Html.fromHtml(it.toString(), Html.FROM_HTML_MODE_COMPACT)
        } else {
          @Suppress("DEPRECATION")
          Html.fromHtml(it.toString())
        }
        builder.setContentText(text)
          .setStyle(NotificationCompat.BigTextStyle().bigText(text))
      }
      appNotification.deepLink?.let {
        builder.setContentIntent(
          PendingIntent.getActivity(
            context,
            0,
            Intent(Intent.ACTION_VIEW, Uri.parse(it)),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              PendingIntent.FLAG_IMMUTABLE
            } else {
              PendingIntent.FLAG_UPDATE_CURRENT
            },
          )
        )
      }
      appNotification.largeIcon?.let {
        val imageLoader = ImageLoaderBuilder(context).build()
        val request = ImageRequestBuilder().data(it).build()
        val result = imageLoader.execute(request)
        if (result is ComposeImageResult) {
          builder.setLargeIcon(result.image)
        }
      }
      notificationManagerCompat.notify(notificationId, builder.build())
    }
  }

  @OptIn(ExperimentalTime::class)
  actual fun notifyTransient(
    notificationId: Int,
    appNotification: AppNotification,
    duration: Duration
  ) {
    notify(notificationId, appNotification)
    scope.launch {
      delay(duration)
      notificationManagerCompat.cancel(notificationId)
    }
  }
}
