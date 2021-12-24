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
package com.twidere.twiderex.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebView

private fun Context.fixForLollipop(): Context {
    return if (Build.VERSION.SDK_INT in 21..22) {
        applicationContext
    } else this
}

class LollipopFixWebView : WebView {

    constructor(context: Context) : super(context.fixForLollipop())
    constructor(context: Context, attrs: AttributeSet?) : super(context.fixForLollipop(), attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.fixForLollipop(),
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context.fixForLollipop(), attrs, defStyleAttr, defStyleRes)

    @Suppress("DEPRECATION")
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        privateBrowsing: Boolean
    ) : super(context.fixForLollipop(), attrs, defStyleAttr, privateBrowsing)

    init {
        isFocusable = true
        isFocusableInTouchMode = true
    }
}
