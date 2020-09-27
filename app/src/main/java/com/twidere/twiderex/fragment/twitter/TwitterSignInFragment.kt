package com.twidere.twiderex.fragment.twitter

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
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.ui.tooling.preview.Preview
import com.twidere.twiderex.R
import com.twidere.twiderex.fragment.JetFragment
import com.twidere.twiderex.viewmodel.twitter.TwitterSignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class TwitterSignInFragment : JetFragment() {
    private val viewModel: TwitterSignInViewModel by viewModels()

    @Preview
    @Composable
    override fun onCompose() {
        val isLoading by viewModel.isLoading.observeAsState()
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading == true) {
                CircularProgressIndicator()
            } else {
                Button(onClick = {
                    activity?.lifecycleScope?.launch {
                        // TODO: dynamic key && secret
                        viewModel.beginOAuth(
                            "MUUBibXUognm6e9vbzrUIqPkt",
                            "l2uWAgQkoHvDfM2PrRFx2WN4h7QIUIktmxyeTAqRo6TkGCtNKy"
                        ) { target ->
                            suspendCoroutine {
                                setFragmentResultListener("request_pin_code") { _, bundle ->
                                    val pinCode = bundle.getString("pin_code")
                                    if (pinCode != null) {
                                        it.resume(pinCode)
                                    } else {
                                        it.resumeWithException(Error("pin code not found"))
                                    }
                                }
                                TwitterSignInFragmentDirections.actionTwitterSignInFragmentToTwitterWebSignInFragment(target).let {
                                    findNavController().navigate(it)
                                }
                            }
                        }
                        findNavController().navigate(R.id.action_twitter_sign_in_fragment_to_home_fragment)
                    }
                }) {
                    Text(text = "Sign In")
                }
            }
        }
    }
}