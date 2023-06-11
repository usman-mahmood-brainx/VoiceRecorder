package com.example.voicerecorder.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.voicerecorder.data.AppRepository

class VoiceChatViewModelFactory(private val repository: AppRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VoiceChatViewModel(repository)as T
    }
}