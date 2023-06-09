package com.example.voicerecorder.Interfaces

import java.io.File

interface AudioRecorderInterface {
    fun start(outputFile : File)
    fun stop()
    fun pause()
    fun resume()
}