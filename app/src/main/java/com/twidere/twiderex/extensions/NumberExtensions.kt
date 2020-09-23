package com.twidere.twiderex.extensions

import android.content.Context
import android.content.res.Resources

@Deprecated("")
val Number.px: Float
    get() = this.toFloat() / Resources.getSystem().displayMetrics.density

fun Number.px(context: Context): Float = this.toFloat() / context.resources.displayMetrics.density