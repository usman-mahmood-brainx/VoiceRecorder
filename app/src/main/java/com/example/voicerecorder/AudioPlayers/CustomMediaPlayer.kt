package com.example.voicerecorder.AudioPlayers

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import com.example.voicerecorder.Interfaces.AudioPlayerInterface
import java.io.File


class CustomMediaPlayer(private val context : Context) : AudioPlayerInterface {
    private var player: MediaPlayer? = null
    private var playbackPosition: Int = 0



    override fun getPlayerName(): String {
        return "Media Player"
    }


    override fun playFile(file: File,onComplete:() -> Unit) {
        MediaPlayer.create(context,file.toUri()).apply {
            player = this
            setOnCompletionListener {
                 onComplete.invoke()
            }

        }
       
        player?.start()
   


    }

    override fun stop() {
        player?.apply {
            stop()
            release()
        }
        player = null
    }

    override fun pause() {
        player?.apply {
            playbackPosition = currentPosition
            pause()
        }
    }

    override fun resume() {
        player?.apply {
            seekTo(playbackPosition)
            start()
        }

    }
}