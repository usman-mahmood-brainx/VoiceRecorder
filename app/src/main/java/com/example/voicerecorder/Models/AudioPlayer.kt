package com.example.voicerecorder.Models

import com.example.voicerecorder.CustomTimer
import com.example.voicerecorder.Interfaces.AudioPlayerInterface

data class AudioPlayer(val player:AudioPlayerInterface,var playerRunning: Boolean,var playerPause:Boolean,val timer: CustomTimer)