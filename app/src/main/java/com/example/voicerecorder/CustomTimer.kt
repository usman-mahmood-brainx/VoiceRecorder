package com.example.voicerecorder

import android.os.CountDownTimer
import java.text.SimpleDateFormat
import java.util.*

class CustomTimer() {
    private var timer: CountDownTimer? = null
    private var isTimerRunning: Boolean = false
    private var elapsedMilliseconds: Long = 0

    fun startTimer(changeRecordingText: (formatedTime:String) -> Unit) {
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedMilliseconds += 1000
                val formattedTime = formatTime(elapsedMilliseconds)
                changeRecordingText(formattedTime)
            }

            override fun onFinish() {

            }
        }
        timer?.start()
        isTimerRunning = true
    }

    fun pauseTimer() {
        if (isTimerRunning) {
            timer?.cancel()
            isTimerRunning = false
        }
    }

    fun resumeTimer() {
       timer?.start()
        isTimerRunning = true
    }

    fun stopTimer() {
        timer?.cancel()
        timer = null
        isTimerRunning = false
        elapsedMilliseconds = 0
    }

    private fun formatTime(milliseconds: Long): String {
        val timeFormat = SimpleDateFormat("m:ss", Locale.getDefault())
        return timeFormat.format(Date(milliseconds))
    }
}
