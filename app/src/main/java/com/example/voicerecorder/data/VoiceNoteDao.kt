package com.example.voicerecorder.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.voicerecorder.Models.VoiceNote


@Dao
interface VoiceNoteDao {
    @Insert
    suspend fun insertVoiceNote(voiceNote: VoiceNote)

    @Insert
    suspend fun insertVoiceNoteList(voiceNotes: List<VoiceNote>)

    @Update
    suspend  fun updateVoiceNote(voiceNote: VoiceNote)

    @Delete
    suspend fun deleteVoiceNote(voiceNote: VoiceNote)

    @Query("SELECT * FROM VoiceNote")
     fun getVoiceNotes(): LiveData<List<VoiceNote>>
}