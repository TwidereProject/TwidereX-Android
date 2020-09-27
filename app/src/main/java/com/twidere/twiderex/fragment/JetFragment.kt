package com.twidere.twiderex.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import com.twidere.twiderex.extensions.compose
import com.twidere.twiderex.ui.TwidereXTheme

abstract class JetFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return compose {
            TwidereXTheme {
                onCompose()
            }
        }
    }

    @Composable
    abstract fun onCompose()
}

