package com.twidere.twiderex.scenes.twitter

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.twidere.twiderex.extensions.navViewModel
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.viewmodel.twitter.TwitterSignInViewModel

@Composable
fun TwitterSignInScene() {
    val viewModel = navViewModel<TwitterSignInViewModel>()
    val isLoading by viewModel.isLoading.observeAsState()
    val navController = AmbientNavController.current
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading == true) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    TODO()
//                    activity?.lifecycleScope?.launch {
//                        // TODO: dynamic key && secret
//                        viewModel.beginOAuth(
//                            "wmtrtTaVOjUnH5pWQp4LDI5Qs",
//                            "E9Q9u2yK0COJae2tLcNEdY75OPA3bxqJiGZQztraHaQUtoI2cu"
//                        ) { target ->
//                            suspendCoroutine {
//                                setFragmentResultListener("request_pin_code") { _, bundle ->
//                                    val pinCode = bundle.getString("pin_code")
//                                    if (pinCode != null) {
//                                        it.resume(pinCode)
//                                    } else {
//                                        it.resumeWithException(Error("pin code not found"))
//                                    }
//                                }
//                                TwitterSignInFragmentDirections.actionTwitterSignInFragmentToTwitterWebSignInFragment(target).let {
//                                    findNavController().navigate(it)
//                                }
//                            }
//                        }
//                        findNavController().navigate(R.id.action_twitter_sign_in_fragment_to_home_fragment)
//                    }
                }
            ) {
                Text(text = "Sign In")
            }
        }
    }
}