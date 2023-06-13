package com.example.voicerecorder

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.voicerecorder.AudioPlayers.VoiceNotePlayer
import com.example.voicerecorder.Utills.Constants.ACTION_STOP_FOREGROUND
import com.example.voicerecorder.Utills.Constants.CHANNEL_ID

class VoiceNoteService : Service() {

    private var myBinder = MyBinder()
    var player: VoiceNotePlayer? = null
    private lateinit var mediaSession:MediaSessionCompat

    private val stopForegroundReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_STOP_FOREGROUND) {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                player?.stop()
            }
        }
    }



    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }



    override fun onDestroy() {
        super.onDestroy()

        // Unregister the BroadcastReceiver when the service is destroyed
        unregisterReceiver(stopForegroundReceiver)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start the service as a foreground service


        // Register the BroadcastReceiver to receive the notification click event
        val filter = IntentFilter(ACTION_STOP_FOREGROUND)
        registerReceiver(stopForegroundReceiver, filter)




        mediaSession = MediaSessionCompat(baseContext,"My audio")
        startForeground(13,createNotification())
        

        // Continue with other service operations

        return START_STICKY
    }

    private fun createNotification(): Notification {

        val stopIntent = Intent(ACTION_STOP_FOREGROUND)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, 0)

        val notification = NotificationCompat.Builder(baseContext,CHANNEL_ID)
            .setContentTitle("Voice Note")
            .setContentText("usman")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_play))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()


        return notification
    }



    inner class MyBinder:Binder(){
        val service:VoiceNoteService
            get() = this@VoiceNoteService
    }
}
