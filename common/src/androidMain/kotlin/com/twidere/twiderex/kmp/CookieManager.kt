package com.twidere.twiderex.kmp

import android.webkit.CookieManager

actual fun clearCookie() {
    CookieManager.getInstance().removeAllCookies {}
}