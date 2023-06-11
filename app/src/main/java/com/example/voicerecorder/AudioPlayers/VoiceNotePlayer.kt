package com.example.voicerecorder.AudioPlayers

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.exoplayer2.Player
import java.io.File

class VoiceNotePlayer(private val context: Context) : CustomExoPlayer(context) {

    private lateinit var progressRunnable :Runnable
    private var handler:Handler? = null
    private val duration
        get() = player?.duration?.toInt()

    fun playVoiceNote(file: File, onComplete: () -> Unit,onReady:(currentPosition:Int) -> Unit) {
        super.playFile(file, onComplete)


        val currentPosition = player?.currentPosition?.toInt()
        val remainingTime = duration!! - currentPosition!!
        handler = Handler(Looper.getMainLooper())
        progressRunnable  = object : Runnable {
            override fun run() {
                val currentPosition = player?.currentPosition?.toInt()
                onReady(currentPosition?:0)
                handler?.postDelayed(this, remainingTime.toLong()) // Update progress every 1 second (adjust as needed)
            }
        }

        player?.apply {
            addListener(object : Player.Listener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when(playbackState){
                        Player.STATE_READY -> {
                            handler?.post(progressRunnable)
                        }


                    }


                }
            })
        }
    }

    override fun pause() {
        super.pause()
        handler?.removeCallbacks(progressRunnable)
    }

    override fun stop() {
        super.stop()
        handler?.removeCallbacks(progressRunnable)
    }

    override fun resume() {
        super.resume()

        val currentPosition = player?.currentPosition?.toInt()
        val remainingTime = duration!! - currentPosition!!
        handler?.postDelayed(progressRunnable,remainingTime.toLong())
    }


}