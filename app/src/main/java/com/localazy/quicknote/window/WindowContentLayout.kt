package com.localazy.quicknote.window

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout

class WindowContentLayout(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var isActiveListener: ((isActive: Boolean) -> Unit)? = null

    fun setIsActiveListener(listener: (isActive: Boolean) -> Unit) {
        this.isActiveListener = listener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> isActiveListener?.invoke(true)
            MotionEvent.ACTION_OUTSIDE -> isActiveListener?.invoke(false)
        }
        return super.onTouchEvent(motionEvent)
    }

    override fun onInterceptTouchEvent(motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> isActiveListener?.invoke(true)
            MotionEvent.ACTION_OUTSIDE -> isActiveListener?.invoke(false)
        }
        return super.onInterceptTouchEvent(motionEvent)
    }
}
