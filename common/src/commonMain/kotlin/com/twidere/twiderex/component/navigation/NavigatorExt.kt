package com.twidere.twiderex.component.navigation

import com.twidere.twiderex.kmp.clearCookie
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.navigation.Root
import com.twidere.twiderex.navigation.twidereXSchema
import com.twidere.twiderex.twitterHosts
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator

suspend fun Navigator.twitterSignInWeb(target: String): String {
    clearCookie()
    return navigateForResult(
        Root.SignIn.Web.Twitter(target)
    ).toString()
}

fun Navigator.openLink(
    link: String,
    deepLink: Boolean =  true,
) {
    if ((link.contains(twidereXSchema) || isTwitterDeeplink(link)) && deepLink) {
        navigate(link)
    } else {
        // openDeepLink(link)
    }
}

fun Navigator.user(
    user: UiUser,
    navOptions: NavOptions?= null,
) {
    navigate(Root.User(user.userKey), navOptions)
}

private fun isTwitterDeeplink(url: String): Boolean {
    twitterHosts.forEach {
        if (url.startsWith(it) && url.length > it.length) {
            return true
        }
    }
    return false
}