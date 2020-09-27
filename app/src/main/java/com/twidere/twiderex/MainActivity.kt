package com.twidere.twiderex

import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentContainerView
import com.twidere.twiderex.extensions.updateMargins
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val rootView: FragmentContainerView by lazy {
        findViewById(R.id.nav_host_fragment)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            rootView.setOnApplyWindowInsetsListener { view, insets ->
                val systemInsets = insets.getInsets(WindowInsets.Type.systemBars())
                rootView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMargins(systemInsets)
                }
                insets
            }
            rootView.setWindowInsetsAnimationCallback(object :
                WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
                override fun onProgress(
                    insets: WindowInsets,
                    animations: MutableList<WindowInsetsAnimation>
                ): WindowInsets {
                    val systemInsets = insets.getInsets(WindowInsets.Type.ime() or WindowInsets.Type.systemBars())
                    rootView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        updateMargins(systemInsets)
                    }
                    return insets
                }
            })
        } else {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }
}
