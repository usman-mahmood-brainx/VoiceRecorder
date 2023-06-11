package com.example.voicerecorder.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicerecorder.Adapters.AudioPlayerAdapter
import com.example.voicerecorder.AudioPlayers.CustomExoPlayer
import com.example.voicerecorder.AudioPlayers.CustomMediaPlayer
import com.example.voicerecorder.AudioPlayers.CustomSoundPool
import com.example.voicerecorder.AudioRecorder
import com.example.voicerecorder.CustomTimer
import com.example.voicerecorder.Models.AudioPlayer
import com.example.voicerecorder.databinding.ActivityDiiferentPlayersBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File

class DiiferentPlayersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiiferentPlayersBinding
    private val recordPermissionFlag: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val recorder by lazy {
        AudioRecorder(applicationContext)
    }
    private val recorderTimer by lazy {
        CustomTimer()
    }

    private var audioFile: File? = null
    private var isRecordingStart = false
    private var isRecordingPause = false

    val requestMultiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted = permissions.all { it.value }
            if (isGranted) {
                recordPermissionFlag.value = true
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiiferentPlayersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListener()

        binding.rvPlayers.layoutManager = LinearLayoutManager(this)

    }

    private fun initListener() {
       binding.btnStartStopRecording.setOnClickListener {
            lifecycleScope.launch {
                recordPermissionFlag.collect{
                   if(it){
                       if (!isRecordingStart) {
                           startRecording()
                       }
                       else{
                           stopRecording()
                       }
                   }
                   else{
                       requestPermission()
                   }
                }
            }
       }

       binding.btnPauseResumeRecording.setOnClickListener {
           pauseResumeRecording()
       }


    }

    private fun startRecording() {
        File(cacheDir, "usmanAudio.mp3").also {
            recorder.start(it)
            audioFile = it

        }
        binding.tvRecordingTime.visibility = View.VISIBLE
        recorderTimer.startTimer{time->
            binding.tvRecordingTime.text = time
        }
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
        audioFile?.let{
            val adapter = AudioPlayerAdapter(getAudioPlayersList(),it)
            binding.rvPlayers.adapter = adapter
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




    private fun requestPermission() {
        val permissionList = mutableListOf<String>()

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.RECORD_AUDIO)
        }

        if (permissionList.isEmpty()) {
            recordPermissionFlag.value = true


        } else {
            permissionList.forEach{
                if(shouldShowRequestPermissionRationale(it)){
                    requestMultiplePermissionsLauncher.launch(permissionList.toTypedArray())
                }
                else{
                    val permissionIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    permissionIntent.data = uri
                    startActivity(permissionIntent)
                }
            }
        }
    }

    fun getAudioPlayersList() : MutableList<AudioPlayer>{
        return mutableListOf(
            AudioPlayer(
                CustomMediaPlayer(applicationContext),
                false,
                false,
                CustomTimer()
            ),
            AudioPlayer(
                CustomExoPlayer(applicationContext),
                false,
                false,
                CustomTimer()
            ),
            AudioPlayer(
                CustomSoundPool(applicationContext),
                false,
                false,
                CustomTimer()
            )
        )
    }


}