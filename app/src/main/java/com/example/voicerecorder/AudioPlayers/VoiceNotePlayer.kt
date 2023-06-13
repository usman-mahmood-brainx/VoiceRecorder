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

    private var progressRunnable: Runnable? = null
    private var handler: Handler? = null
    private val duration
        get() = player?.duration?.toInt()
    private var player: ExoPlayer? = null
    private var currentFile :File?= null
    private var playbackPosition: Long = 0
    private var isPlayerRunning: Boolean = false


    override fun getPlayerName(): String {
        return "Exo Player"
    }

    fun isPlayerRunning(): Boolean {
        return isPlayerRunning
    }

    fun playVoiceNote(file: File, onComplete: () -> Unit, onReady: (currentPosition: Int) -> Unit) {
        if(currentFile == null){
            currentFile = file
        }
        else if(currentFile != file){
            player = null
            handler = null
            progressRunnable = null
            currentFile = file

        }
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
                            progressRunnable?.let {
                                handler?.post(progressRunnable!!)
                            }
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
        isPlayerRunning = true

    }



    override fun stop() {
        player?.apply {
            stop()
            release()
        }
        player = null
        progressRunnable?.let {
            handler?.removeCallbacks(progressRunnable!!)
        }
        isPlayerRunning = false
    }



    fun moveToPosition(position: Long,file: File) {
        if(currentFile !=null && currentFile == file){
            player?.seekTo(position)
        }

    }

    override fun pause() {
        player?.playWhenReady = false
        playbackPosition = player?.currentPosition ?: 0
        isPlayerRunning = false
    }

    override fun resume() {
        player?.playWhenReady = true
        player?.seekTo(playbackPosition)
        isPlayerRunning = true
    }

}