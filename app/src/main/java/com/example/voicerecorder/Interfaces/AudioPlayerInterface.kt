package com.example.voicerecorder.Interfaces

import java.io.File

interface  AudioPlayerInterface {

    fun getPlayerName():String

    fun playFile(file:File,onComplete: () -> Unit) {

    }
    fun stop()
    fun pause(){

    }
    fun resume(){

    }

    fun seekto(position: Long){

    }

}