package com.example.voicerecorder.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.voicerecorder.Models.VoiceNote


@Database(entities = [VoiceNote::class], version = 1)
abstract class VoiceNoteDatabase : RoomDatabase() {

    abstract fun voiceNoteDao(): VoiceNoteDao

    // Thread Safe Approach
    companion object{
        // Whenever our instance value assigned its updated value will be available to all threads
        @Volatile
        private var INSTANCE:VoiceNoteDatabase? = null

        fun getDatabase(context: Context):VoiceNoteDatabase{
            if(INSTANCE == null){
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext, VoiceNoteDatabase::class.java, "VoiceNoteDb"
                    ).build()
                }

            }
            return INSTANCE!!
        }
    }
}