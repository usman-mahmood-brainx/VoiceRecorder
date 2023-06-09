package com.example.voicerecorder

import android.os.CountDownTimer
import com.example.voicerecorder.Interfaces.TimerListener
import java.text.SimpleDateFormat
import java.util.*

class CustomTimer(private val timerListener: TimerListener) {
    private var timer: CountDownTimer? = null
    private var isTimerRunning: Boolean = false
    private var elapsedMilliseconds: Long = 0

    fun startTimer() {
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedMilliseconds += 1000
                val formattedTime = formatTime(elapsedMilliseconds)
                timerListener.changeRecordingText(formattedTime)
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
        if (!isTimerRunning) {
            startTimer()
        }
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
