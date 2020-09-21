package com.twidere.twiderex.extensions

import android.content.res.Resources
import android.os.Build
import org.ocpsoft.prettytime.PrettyTime
import java.text.DateFormat
import java.util.*


private val prettyTime = PrettyTime(if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
    Resources.getSystem().configuration.locales[0]
} else {
    Resources.getSystem().configuration.locale
})

private val SECOND_MILLIS = 1000
private val MINUTE_MILLIS = 60 * SECOND_MILLIS
private val HOUR_MILLIS = 60 * MINUTE_MILLIS
private val DAY_MILLIS = 24 * HOUR_MILLIS

fun Long.humanizedTimestamp(): String {
    val date = Date(this)
    return if (System.currentTimeMillis() - date.time > 3 * HOUR_MILLIS) {
        DateFormat.getDateTimeInstance().format(date)
    } else {
        prettyTime.format(date)
    }
}