package com.example.voicerecorder

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.voicerecorder.AudioPlayers.CustomSoundPool
import com.example.voicerecorder.Interfaces.TimerListener
import com.example.voicerecorder.databinding.ActivityMainBinding
import java.io.File
import java.io.FileInputStream


class MainActivity : AppCompatActivity() {

    val requestMultiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val isGranted = permissions.all { it.value }
            if (isGranted) {
                startRecording()

            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }

        }

    private lateinit var binding: ActivityMainBinding

    private val recorder by lazy {
        AudioRecorder(applicationContext)
    }
    private val player by lazy {
        CustomSoundPool(applicationContext)
    }
    private lateinit var recorderTimer: CustomTimer
    private lateinit var playerTimer: CustomTimer
    private var audioFile: File? = null
    private var isRecordingStart = false
    private var isRecordingPause = false
    private var isPlayerRunning = false
    private var isPlayerPause = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = Intent(this,DiiferentPlayersActivity::class.java)
        startActivity(intent)
        finish()
        recorderTimer = CustomTimer(object : TimerListener {
            override fun changeRecordingText(time: String) {
                binding.tvRecordingTime.text = time
            }
        })
        playerTimer =  CustomTimer(object : TimerListener {
            override fun changeRecordingText(time: String) {
                binding.tvPlayerTime.text = time
            }
        })


        initListeners()
    }


    private fun initListeners() {
        binding.btnStartStopRecording.setOnClickListener {
            if (!isRecordingStart) {
                requestPermission()
            } else {
                stopRecording()
                saveRecording()
            }
        }

        binding.btnPauseResumeRecording.setOnClickListener {
            pauseResumeRecording()
        }

        binding.btnStartResetPlayer.setOnClickListener {
            if(!isPlayerRunning) {
                play()
            }
            else{
                stopPlayer()
            }
        }

        binding.btnPauseResumePlayer.setOnClickListener {
            pauseResumePlayer()
        }
    }


    private fun startRecording() {
        File(filesDir, "usmanAudio.mp3").also {
            recorder.start(it)
            audioFile = it
        }
        binding.tvRecordingTime.visibility = View.VISIBLE
        recorderTimer.startTimer()
        isRecordingStart = true
        binding.btnStartStopRecording.text = "Stop"
        binding.btnPauseResumeRecording.apply {
            isEnabled = true
            text = "Pause"
        }
    }

    private fun stopRecording() {
        recorder.stop()
        recorderTimer.stopTimer()
        isRecordingStart = false
        binding.btnStartStopRecording.text = "Start"
        binding.btnPauseResumeRecording.apply {
            text = "Pause"
            isEnabled = false
        }
    }

    private fun saveRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, audioFile?.name ?: "random.mp3")
                put(MediaStore.MediaColumns.MIME_TYPE, "audio/*")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
            }

            val resolver = contentResolver
            val audioUri =
                resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)

            audioUri?.let { uri ->
                resolver.openOutputStream(uri)?.use { outputStream ->
                    val fileInputStream = FileInputStream(audioFile)
                    fileInputStream.copyTo(outputStream)
                    fileInputStream.close()

                    Toast.makeText(this, "Audio file saved", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun pauseResumeRecording(){
        if(!isRecordingPause){
            recorder.pause()
            recorderTimer.pauseTimer()
            binding.btnPauseResumeRecording.text = "Resume"
            isRecordingPause = true
        }
        else{
            recorder.resume()
            recorderTimer.resumeTimer()
            binding.btnPauseResumeRecording.text = "Pause"
            isRecordingPause = false
        }
    }

    private fun play() {
        if(!isRecordingStart && audioFile!=null) {
            player?.playFile(audioFile ?: return,{
                audioPlaybackCompleted()
            })
            binding.btnStartResetPlayer.text = "Reset"
            binding.btnPauseResumePlayer.apply {
                isEnabled = true
                text = "Pause"
            }
            isPlayerRunning = true
            binding.tvPlayerTime.visibility = View.VISIBLE
            playerTimer.startTimer()
        }
        else{
            Toast.makeText(this,"No Audio",Toast.LENGTH_SHORT).show()
        }

    }

    private fun stopPlayer() {
        player?.stop()
        binding.btnStartResetPlayer.text = "Start"
        binding.btnPauseResumePlayer.apply {
            isEnabled = false
            text = "Pause"
        }
        isPlayerRunning = false
        playerTimer.stopTimer()
    }

    private fun pauseResumePlayer(){
        if(!isPlayerPause){
            player.pause()
            playerTimer.pauseTimer()
            binding.btnPauseResumePlayer.text = "Resume"
            isPlayerPause = true
        }
        else{
            player.resume()
            playerTimer.resumeTimer()
            binding.btnPauseResumePlayer.text = "Pause"
            isPlayerPause = false
        }
    }

    private fun audioPlaybackCompleted() {
        stopPlayer()
    }


    private fun requestPermission() {
        val permissionList = mutableListOf<String>()

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.RECORD_AUDIO)
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionList.isEmpty()) {
            startRecording()
        } else {
            requestMultiplePermissionsLauncher.launch(permissionList.toTypedArray())
        }
    }


}