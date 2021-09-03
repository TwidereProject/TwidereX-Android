/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.component.foundation

import android.content.Context
import android.util.AttributeSet
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.TimeBar

class RemainingTimeExoPlayer(builder: Builder) : SimpleExoPlayer(builder) {
    override fun getContentPosition(): Long {
        return super.getContentPosition() - contentDuration
    }
}

class RemainingTimeBar : DefaultTimeBar {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        timebarAttrs: AttributeSet?
    ) : this(context, attrs, defStyleAttr, timebarAttrs, 0)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        timebarAttrs: AttributeSet?,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, timebarAttrs, defStyleRes)

    private var duration: Long = 0
    private val listeners = mutableListOf<RemainingScrubListenerWrapper>()
    override fun setDuration(duration: Long) {
        this.duration = duration
        super.setDuration(duration)
    }
    override fun setPosition(position: Long) {
        super.setPosition(if (position < 0) duration + position else position)
    }

    override fun addListener(listener: TimeBar.OnScrubListener) {
        val wrapper = RemainingScrubListenerWrapper(listener) {
            if (it < 0) it else it - duration
        }.also {
            listeners.add(it)
        }
        super.addListener(wrapper)
    }

    override fun removeListener(listener: TimeBar.OnScrubListener) {
        listeners.removeAll { it.listener == listener }
        super.removeListener(listener)
    }

    private class RemainingScrubListenerWrapper(
        val listener: TimeBar.OnScrubListener,
        private val transform: (position: Long) -> Long
    ) : TimeBar.OnScrubListener {

        override fun onScrubStart(timeBar: TimeBar, position: Long) {
            listener.onScrubStart(timeBar, transform(position))
        }

        override fun onScrubMove(timeBar: TimeBar, position: Long) {
            listener.onScrubMove(timeBar, transform(position))
        }

        override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
            // do not transform position here
            listener.onScrubStop(timeBar, position, canceled)
        }
    }
}
