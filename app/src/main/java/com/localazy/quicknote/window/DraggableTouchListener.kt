package com.localazy.quicknote.window

import android.content.Context
import android.graphics.Point
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import java.util.Timer
import kotlin.concurrent.timerTask
import kotlin.math.hypot

fun View.registerDraggableTouchListener(
    getInitialPosition: () -> Point,
    onViewMove: (Point) -> Unit
) {
    WindowHeaderTouchListener(context, this, getInitialPosition, onViewMove)
}

class WindowHeaderTouchListener(
    context: Context,
    private val view: View,
    private val getInitialPosition: () -> Point,
    private val onViewMove: (Point) -> Unit
) : View.OnTouchListener {

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private val longClickInterval = ViewConfiguration.getLongPressTimeout().toLong()

    private val onDownPosition = Point()
    private var initialPosition = Point()

    private var moving = false
    private var longClickPerformed = false
    private var timer: Timer? = null

    init {
        view.setOnTouchListener(this)
    }

    private fun scheduleLongClickTimer() {
        timer?.cancel()
        timer = Timer().apply {
            schedule(timerTask {
                if (!moving && !longClickPerformed) {
                    view.post { view.performLongClick() }
                    longClickPerformed = true
                    cancelLongClickTimer()

                }
            }, longClickInterval)
        }
    }

    private fun cancelLongClickTimer() {
        timer?.cancel()
        timer = null
    }

    override fun onTouch(
        view: View,
        motionEvent: MotionEvent
    ): Boolean {

        when (motionEvent.action) {

            MotionEvent.ACTION_DOWN -> {
                onDownPosition.x = motionEvent.rawX.toInt()
                onDownPosition.y = motionEvent.rawY.toInt()
                initialPosition = getInitialPosition()
                moving = false
                longClickPerformed = false
                scheduleLongClickTimer()
            }

            MotionEvent.ACTION_MOVE -> {
                if (longClickPerformed) {
                    return true
                }
                val deltaX = motionEvent.rawX - onDownPosition.x
                val deltaY = motionEvent.rawY - onDownPosition.y
                if (moving || hypot(deltaX, deltaY) > touchSlop) {
                    cancelLongClickTimer()
                    val newX = initialPosition.x + deltaX.toInt()
                    val newY = initialPosition.y + deltaY.toInt()
                    onViewMove(Point(newX, newY))
                    moving = true
                }
            }

            MotionEvent.ACTION_UP -> {
                cancelLongClickTimer()
                if (!moving && !longClickPerformed) {
                    view.performClick()
                }
            }
        }

        return true
    }
}
