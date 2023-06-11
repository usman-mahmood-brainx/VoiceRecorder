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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicerecorder.Adapters.VoiceNoteAdapter
import com.example.voicerecorder.AudioRecorder
import com.example.voicerecorder.CustomTimer
import com.example.voicerecorder.Models.VoiceNote
import com.example.voicerecorder.R
import com.example.voicerecorder.ViewModels.VoiceChatViewModel
import com.example.voicerecorder.ViewModels.VoiceChatViewModelFactory
import com.example.voicerecorder.VoiceChatApplication
import com.example.voicerecorder.databinding.ActivityVoiceChatBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VoiceChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVoiceChatBinding
    private lateinit var voiceChatViewModel: VoiceChatViewModel


    private val recorder by lazy {
        AudioRecorder(applicationContext)
    }

    private val recorderTimer by lazy {
        CustomTimer()
    }

    private var audioFile: File? = null
    private var isRecordingStart = false
    private var isRecordingPause = false

    private val recordPermissionFlag: MutableStateFlow<Boolean> = MutableStateFlow(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = (application as VoiceChatApplication).appRepository
        voiceChatViewModel = ViewModelProvider(this,VoiceChatViewModelFactory(repository)).get(VoiceChatViewModel::class.java)

        val voiceNoteAdapter = VoiceNoteAdapter(emptyList(),this,binding.rvVoiceNotes)
        binding.rvVoiceNotes.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = false
        }
        binding.rvVoiceNotes.adapter = voiceNoteAdapter

        voiceChatViewModel.voiceNotes.observe(this,{
            it?.let{
                voiceNoteAdapter.setList(it.reversed())
                binding.rvVoiceNotes.scrollToPosition(0)
            }
        })

        initListener()




    }

    private fun initListener() {
        binding.btnDelete.visibility = View.INVISIBLE
        binding.btnRecordSend.setOnClickListener {
            lifecycleScope.launch {
                recordPermissionFlag.collect{
                    if(it){
                        if (!isRecordingStart) {
                            startRecording()
                        }
                        else{
                            sendRecording()
                        }
                    }
                    else{
                        requestPermission()
                    }
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            cancelRecording()
        }


    }

    private fun startRecording() {
       File(filesDir, createAudioFile()).also {
            recorder.start(it)
            audioFile = it
        }

        binding.tvRecordTimer.visibility = View.VISIBLE
        recorderTimer.startTimer{time->
            binding.tvRecordTimer.text = time
        }
        isRecordingStart = true
        binding.btnRecordSend.setImageResource(R.drawable.ic_send)
        binding.btnDelete.visibility = View.VISIBLE
    }

    private fun sendRecording(){
        recorder.stop()
        recorderTimer.stopTimer()
        isRecordingStart = false
        binding.btnRecordSend.setImageResource(R.drawable.ic_mic)
        binding.tvRecordTimer.visibility = View.INVISIBLE
        binding.btnDelete.visibility = View.INVISIBLE
        audioFile?.apply{
           voiceChatViewModel.addVoiceNote(
               VoiceNote(0,audioFile?.name)
           )
        }
        audioFile = null
    }

    private fun cancelRecording(){
        recorder.stop()
        recorderTimer.stopTimer()
        binding.btnRecordSend.setImageResource(R.drawable.ic_mic)
        binding.tvRecordTimer.visibility = View.INVISIBLE
        binding.btnDelete.visibility = View.INVISIBLE
        audioFile = null
    }



    fun createAudioFile(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "usman_voicenote_$timeStamp"
    }

    val requestMultiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted = permissions.all { it.value }
            if (isGranted) {
                recordPermissionFlag.value = true
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
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
                if(!shouldShowRequestPermissionRationale(it)){
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



}