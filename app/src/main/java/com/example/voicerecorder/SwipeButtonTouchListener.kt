package com.example.voicerecorder

import android.content.Context
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.WindowManager

class SwipeButtonTouchListener(context: Context) : View.OnTouchListener {
    private val screenWidth: Int
    private val middleThreshold: Float

    private var initialX: Float = 0.toFloat()
    private var initialY: Float = 0.toFloat()
    private var velocityTracker: VelocityTracker? = null
    private var swipeThresholdVelocity: Float = 0.toFloat()

    init {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels

        // Calculate the swipe threshold and middle threshold based on the screen width
        swipeThresholdVelocity = screenWidth * SWIPE_THRESHOLD_FACTOR
        middleThreshold = screenWidth / 2f
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                velocityTracker = VelocityTracker.obtain()
                velocityTracker?.addMovement(motionEvent)

                initialX = motionEvent.x
                initialY = motionEvent.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.addMovement(motionEvent)

                val dx = motionEvent.x - initialX

                if (view.translationX == 0f && dx > 0) {
                    // Button is in default position, restrict swiping to the left
                    return true
                }

                // Calculate the new translationX value
                val newTranslationX = view.translationX + dx

                // Limit the translation to the middle of the screen
                view.translationX = newTranslationX.coerceIn(-middleThreshold, middleThreshold)
                return true
            }
            MotionEvent.ACTION_UP , MotionEvent.ACTION_CANCEL -> {
                velocityTracker?.addMovement(motionEvent)
                velocityTracker?.computeCurrentVelocity(1000)

                val velocityX = velocityTracker?.xVelocity ?: 0f

                if (velocityX > swipeThresholdVelocity) {
                    // Swipe to the right
                } else if (velocityX < -swipeThresholdVelocity) {
                    // Swipe to the left
                } else {
                    // Animate the button back to the default position
                    view.animate().translationX(0f).start()
                }

                velocityTracker?.recycle()
                velocityTracker = null

                return true
            }
        }

        return false
    }

    companion object {
        private const val SWIPE_THRESHOLD_FACTOR = 0.3f // Adjust this value as needed
    }
}
