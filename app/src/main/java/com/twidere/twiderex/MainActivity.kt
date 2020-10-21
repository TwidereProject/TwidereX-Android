/*
 *  TwidereX
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of TwidereX.
 * 
 *  TwidereX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  TwidereX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with TwidereX. If not, see <http://www.gnu.org/licenses/>.
 */
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
    private var ignoreWindowInsets = false
    val rootView: FragmentContainerView by lazy {
        findViewById(R.id.nav_host_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            rootView.setOnApplyWindowInsetsListener { _, insets ->
                if (!ignoreWindowInsets) {
                    val systemInsets =
                        insets.getInsets(WindowInsets.Type.ime() or WindowInsets.Type.systemBars())
                    rootView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        updateMargins(systemInsets)
                    }
                }
                insets
            }
            rootView.setWindowInsetsAnimationCallback(object :
                WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
                override fun onPrepare(animation: WindowInsetsAnimation) {
                    super.onPrepare(animation)
                    ignoreWindowInsets = true
                }

                override fun onEnd(animation: WindowInsetsAnimation) {
                    super.onEnd(animation)
                    ignoreWindowInsets = false
                }

                override fun onProgress(
                    insets: WindowInsets,
                    animations: MutableList<WindowInsetsAnimation>
                ): WindowInsets {
                    val systemInsets =
                        insets.getInsets(WindowInsets.Type.ime() or WindowInsets.Type.systemBars())
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
