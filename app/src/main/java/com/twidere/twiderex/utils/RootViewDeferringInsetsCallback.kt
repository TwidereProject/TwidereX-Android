package com.twidere.twiderex.utils

import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.R)
class RootViewDeferringInsetsCallback(
    val persistentInsetTypes: Int,
    val deferredInsetTypes: Int,
    private val applyPadding: (left: Int, top: Int, right: Int, bottom: Int) -> Unit,
) : WindowInsetsAnimation.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE),
    View.OnApplyWindowInsetsListener {
    init {
        require(persistentInsetTypes and deferredInsetTypes == 0) {
            "persistentInsetTypes and deferredInsetTypes can not contain any of " +
                    " same WindowInsets.Type values"
        }
    }

    private var view: View? = null
    private var lastWindowInsets: WindowInsets? = null
    private var deferredInsets = false

    override fun onApplyWindowInsets(v: View, windowInsets: WindowInsets): WindowInsets {
        view = v
        lastWindowInsets = windowInsets

        val types = when {
            deferredInsets -> persistentInsetTypes
            else -> persistentInsetTypes or deferredInsetTypes
        }

        val typeInsets = windowInsets.getInsets(types)
        applyPadding.invoke(typeInsets.left, typeInsets.top, typeInsets.right, typeInsets.bottom)
        return WindowInsets.CONSUMED
    }

    override fun onPrepare(animation: WindowInsetsAnimation) {
        if (animation.typeMask and deferredInsetTypes != 0) {
            deferredInsets = true
        }
    }

    override fun onProgress(
        insets: WindowInsets,
        runningAnims: List<WindowInsetsAnimation>
    ): WindowInsets {
        val typeInsets = insets.getInsets(persistentInsetTypes or deferredInsetTypes)
        applyPadding.invoke(typeInsets.left, typeInsets.top, typeInsets.right, typeInsets.bottom)
        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimation) {
        if (deferredInsets && (animation.typeMask and deferredInsetTypes) != 0) {
            deferredInsets = false
            if (lastWindowInsets != null) {
                view?.dispatchApplyWindowInsets(lastWindowInsets!!)
            }
        }
    }
}
