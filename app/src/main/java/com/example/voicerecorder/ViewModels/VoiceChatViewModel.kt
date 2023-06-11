package com.example.voicerecorder.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voicerecorder.Models.VoiceNote
import com.example.voicerecorder.data.AppRepository
import kotlinx.coroutines.launch

class VoiceChatViewModel(private val appRepository: AppRepository):ViewModel() {
   
    val voiceNotes: LiveData<List<VoiceNote>>
        get() = appRepository.voiceNotes

    fun addVoiceNote(voiceNote: VoiceNote){
        viewModelScope.launch {
            appRepository.addVoiceNote(voiceNote)
        }
    }
}