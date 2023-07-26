package com.example.voicerecorder

import android.app.Application
import com.example.voicerecorder.repository.AppRepository
import com.example.voicerecorder.room.VoiceNoteDatabase

class VoiceChatApplication: Application() {

    lateinit var appRepository: AppRepository
    override fun onCreate() {
        super.onCreate()
        initialize()
    }


    private fun initialize(){
        val database = VoiceNoteDatabase.getDatabase(applicationContext)
        appRepository = AppRepository(database)

    }
}