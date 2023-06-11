package com.example.voicerecorder.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "VoiceNote")
data class VoiceNote(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val filePath: String?
)
