package com.example.voicerecorder.recorder

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Build
import android.widget.Toast
import com.example.voicerecorder.Interfaces.AudioRecorderInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AudioRecorder(private val context:Context) : AudioRecorderInterface {
    var recorder:MediaRecorder? = null
    private fun createRecorder():MediaRecorder{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun start(outputFile: File) {
        val audioSource = if (isEarphoneConnected()) {
            MediaRecorder.AudioSource.CAMCORDER
        } else {
            MediaRecorder.AudioSource.MIC
        }

        CoroutineScope(Dispatchers.IO).launch {
            createRecorder().apply {
                setAudioSource(audioSource)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(FileOutputStream(outputFile).fd)

                try {
                    prepare()
                } catch (e: IOException) {
                    Toast.makeText(context, "prepare failed", Toast.LENGTH_SHORT).show()
                }
                start()
                recorder = this
            }
        }
    }

    override fun stop() {
        recorder?.apply{
            stop()
            reset()
            release()
        }
        recorder = null
    }

    override fun pause() {
        recorder?.apply {
            pause()
        }
    }


    override fun resume() {
        recorder?.apply {
            resume()
        }
    }

    private fun isEarphoneConnected(): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

        for (device in audioDevices) {
            if (device.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                device.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                device.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO ||
                device.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
            ) {
                return true
            }
        }

        return false
    }
}