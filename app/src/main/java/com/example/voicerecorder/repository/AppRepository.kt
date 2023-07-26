package com.example.voicerecorder.repository

import androidx.lifecycle.LiveData
import com.example.voicerecorder.Models.VoiceNote
import com.example.voicerecorder.room.VoiceNoteDatabase

class AppRepository(private val voiceNoteDatabase: VoiceNoteDatabase) {


    val voiceNotes: LiveData<List<VoiceNote>>
        get() =  voiceNoteDatabase.voiceNoteDao().getVoiceNotes()


    suspend fun addVoiceNote(voiceNote: VoiceNote){
        voiceNoteDatabase.voiceNoteDao().insertVoiceNote(voiceNote)
    }
}
