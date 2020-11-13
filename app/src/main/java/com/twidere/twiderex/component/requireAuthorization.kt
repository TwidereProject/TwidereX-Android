package com.twidere.twiderex.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.navigation.compose.navigate
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.AmbientActivity
import com.twidere.twiderex.ui.AmbientNavController
import java.util.UUID

private val key = UUID.randomUUID().toString()

@Composable
fun requireAuthorization(
    content: @Composable () -> Unit,
) {
    val account = AmbientActiveAccount.current
    if (account == null) {
        val navController = AmbientNavController.current
        val activity = AmbientActivity.current
        val (isSignInShown, setIsSignInShown) = savedInstanceState(key = key) { false }
        if (!isSignInShown) {
            setIsSignInShown(true)
            navController.navigate(Route.SignIn.Twitter)
        } else {
            activity.finish()
        }
    } else {
        content.invoke()
    }
}
