package com.example.voicerecorder.AudioPlayers

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import com.example.voicerecorder.Interfaces.AudioPlayerInterface
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import java.io.File

class VoiceNotePlayer(private val context: Context) :AudioPlayerInterface {

    private lateinit var progressRunnable: Runnable
    private var handler: Handler? = null
    private val duration
        get() = player?.duration?.toInt()
    private var player: ExoPlayer? = null
    private var currentFile :File?= null


    override fun getPlayerName(): String {
        return "Exo Player"
    }



    fun playVoiceNote(file: File, onComplete: () -> Unit, onReady: (currentPosition: Int) -> Unit) {
        currentFile = file
        val mediaItem = MediaItem.fromUri(file!!.toUri())
        ExoPlayer.Builder(context).build().apply {
            player = this
            setMediaItem(mediaItem)
            playWhenReady = true
            prepare()
            play()
            addListener(object : Player.Listener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        ExoPlayer.STATE_ENDED -> {
                            onComplete.invoke()
                        }
                        ExoPlayer.STATE_READY -> {
                            handler?.post(progressRunnable)
                        }
                    }
                }
            })
        }

        val currentPosition = player?.currentPosition?.toInt()
        val remainingTime = duration!! - currentPosition!!
        handler = Handler(Looper.getMainLooper())

        progressRunnable = object : Runnable {
            override fun run() {
                val currentPosition = player?.currentPosition?.toInt()
                onReady(currentPosition ?: 0)
                handler?.postDelayed(
                    this,
                    remainingTime.toLong()
                )
            }
        }


    }

    override fun pause() {
        player?.playWhenReady = false
        handler?.removeCallbacks(progressRunnable)
    }

    override fun stop() {
        player?.apply {
            stop()
            release()
        }
        player = null
        handler?.removeCallbacks(progressRunnable)
    }

    override fun resume() {

        player?.playWhenReady = true
        val currentPosition = player?.currentPosition?.toInt()
        val remainingTime = duration!! - currentPosition!!
        handler?.postDelayed(progressRunnable, remainingTime.toLong())
    }


    override fun seekto(position: Long) {
        player?.seekTo(position)
    }

}