package com.example.voicerecorder.Adapters

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicerecorder.AudioPlayers.VoiceNotePlayer
import com.example.voicerecorder.Models.VoiceNote
import com.example.voicerecorder.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VoiceNoteAdapter(
    private var voiceNoteList: List<VoiceNote>,
    private val context:Context,
    private val recyclerView:RecyclerView
) : RecyclerView.Adapter<VoiceNoteAdapter.ViewHolder>() {

    private var currentPlayingPosition: Int = -1


    fun setList(list : List<VoiceNote>){
        voiceNoteList = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_voice_note, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val voiceNote=  voiceNoteList[position]

        holder.apply {
            bind(voiceNote)


            btnPlayPause.setOnClickListener{
                if(!isStart && !isPlaying) {
                   playAudio()
                    btnPlayPause.setImageResource(R.drawable.ic_pause)
                }
                else{
                    pauseResumeAudio()
                }

                for (i in 0 until voiceNoteList.size) {
                    if (i != position) {
                        val otherHolder = recyclerView.findViewHolderForAdapterPosition(i) as? ViewHolder
                        if(otherHolder?.isPlaying == true && otherHolder?.isStart == true){
                            otherHolder?.pauseResumeAudio()
                            Log.d("Recording ","Pause")
                        }

                    }
                }
            }



            seekBarVn.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    // Update the player's position based on the SeekBar progress
                    if (fromUser) {
                        player?.seekto(progress.toLong())
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Not needed
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // Not needed
                }
            })

        }
    }

    override fun getItemCount(): Int {
        return voiceNoteList.size
    }

    inner class ViewHolder(VoiceNoteItemView: View) : RecyclerView.ViewHolder(VoiceNoteItemView) {
        val tvTimer: TextView = VoiceNoteItemView.findViewById(R.id.tv_timer_vn)
        val btnPlayPause: ImageButton = VoiceNoteItemView.findViewById(R.id.btn_play_pause_vn)
        val seekBarVn: SeekBar = VoiceNoteItemView.findViewById(R.id.seekBar_vn)
        val player = VoiceNotePlayer(context)
        var isStart: Boolean = false
        var isPlaying:Boolean=false
        var audioFile:File? =null
        var duration:Int = 0

        fun bind(voiceNote: VoiceNote) {
            // Set data to views
            val file =  File(context.filesDir, voiceNote.filePath)
            audioFile =  file
            duration = getAudioDuration(file)
            tvTimer.text = formatTime(duration.toLong())
            seekBarVn.max = duration
            seekBarVn.progress=0

        }


         fun playAudio() {
            player.playVoiceNote(
                file = audioFile!!,
                onComplete = { stopAudio() },
                onReady= { currentPosition->
                   seekBarVn.progress = currentPosition
                    tvTimer.text = formatTime(currentPosition.toLong())
                }
            )
             player.seekto(seekBarVn.progress.toLong())
             isStart = true
             isPlaying = true
        }

        fun pauseResumeAudio(){
            if(isPlaying){
                player.pause()
                btnPlayPause.setImageResource(R.drawable.ic_play)
                isPlaying = false
            }
            else{
                player.resume()
                player.seekto(seekBarVn.progress.toLong())
                btnPlayPause.setImageResource(R.drawable.ic_pause)
                isPlaying = true
            }

        }

        private fun stopAudio(){
            player?.stop()
            isStart = false
            isPlaying = false
            btnPlayPause.setImageResource(R.drawable.ic_play)
            seekBarVn.progress = 0
            tvTimer.text = formatTime(duration.toLong())
        }

        private fun formatTime(milliseconds: Long): String {
            val timeFormat = SimpleDateFormat("m:ss", Locale.getDefault())
            return timeFormat.format(Date(milliseconds))
        }

        private fun getAudioDuration(file: File): Int {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(file.absolutePath)
            mediaPlayer.prepare()
            val duration = mediaPlayer.duration
            mediaPlayer.release()
            return duration
        }


    }


}