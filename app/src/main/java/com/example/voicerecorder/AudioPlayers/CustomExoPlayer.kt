package com.example.voicerecorder.AudioPlayers


import android.content.Context
import androidx.core.net.toUri
import com.example.voicerecorder.Interfaces.AudioPlayerInterface
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import java.io.File

class CustomExoPlayer(private val context: Context) : AudioPlayerInterface {
    private var player: ExoPlayer? = null
    private var playbackPosition: Long = 0

    override fun getPlayerName(): String {
        return "Exo Player"
    }


    override fun playFile(file: File, onComplete: () -> Unit) {
        val mediaItem = MediaItem.fromUri(file!!.toUri())
        ExoPlayer.Builder(context).build().apply {
            player = this
            setMediaItem(mediaItem)
            playWhenReady = true
            prepare()
            play()
            addListener(object : Player.Listener{
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == ExoPlayer.STATE_ENDED) {
                        onComplete.invoke()
                    }
                }
            })


        }


    }

    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }

    override fun pause() {
        player?.playWhenReady = false
        playbackPosition = player?.currentPosition ?: 0
    }

    override fun resume() {
        player?.playWhenReady = true
        player?.seekTo(playbackPosition)
    }
}
