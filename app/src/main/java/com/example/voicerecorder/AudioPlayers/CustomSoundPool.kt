package com.example.voicerecorder.AudioPlayers

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import com.example.voicerecorder.Interfaces.AudioPlayerInterface
import java.io.File

class CustomSoundPool(private val context: Context) : AudioPlayerInterface {


    private var soundPool: SoundPool? = null
    private var soundId: Int? = null
    private var isPlaying: Boolean = false
    private var handler:Handler? = null
    private var progressRunable:Runnable? =null

    override fun getPlayerName(): String {
        return "Sound Pool"
    }


    override fun playFile(file: File, onComplete: () -> Unit) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        soundId = soundPool?.load(file.absolutePath, 1)
        soundPool?.setOnLoadCompleteListener { _, _, _ ->
            soundId?.let { soundPool?.play(it, 1f, 1f, 0, 0, 1f) }
        }
        isPlaying = true

        handler = Handler(Looper.getMainLooper())
        progressRunable = Runnable {
            soundPool?.release()
            onComplete.invoke()
        }

        handler?.postDelayed(progressRunable!!,getAudioDuration(file))



    }

    override fun stop() {
        if (isPlaying) {
            soundPool?.stop(soundId!!)
            soundPool?.release()
            soundId = null
            soundPool = null
            isPlaying = false
        }
    }

    override fun pause() {
        if (isPlaying) {
            soundPool?.pause(soundId!!)
            isPlaying = false
        }
    }

    override fun resume() {
        if (!isPlaying) {
            soundPool?.resume(soundId!!)
            isPlaying = true
        }
    }

    fun getAudioDuration(file: File): Long {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(file.absolutePath)
        mediaPlayer.prepare()
        val duration = mediaPlayer.duration
        mediaPlayer.release()
        return duration.toLong()
    }
}