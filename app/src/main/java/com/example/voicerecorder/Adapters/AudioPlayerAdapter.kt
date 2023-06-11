package com.example.voicerecorder.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voicerecorder.Models.AudioPlayer
import com.example.voicerecorder.R
import java.io.File

class AudioPlayerAdapter(private var audioPlayerList: List<AudioPlayer>, val audioFile: File) : RecyclerView.Adapter<AudioPlayerAdapter.ViewHolder>() {

    fun setList(list : List<AudioPlayer>){
        audioPlayerList = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_audio_player, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val audioPlayer =  audioPlayerList[position]
        holder.apply {
            tvPlayerName.text = audioPlayer.player.getPlayerName()
            btnStartStop.setOnClickListener {
                if(!audioPlayer.playerRunning) {
                    play(audioPlayer,this)
                }
                else{
                    stopPlayer(audioPlayer,this)
                }
            }

            btnPauseResume.setOnClickListener {
                pauseResumePlayer(audioPlayer,this)
            }
        }
    }

    override fun getItemCount(): Int {
        return audioPlayerList.size
    }

    class ViewHolder(TopRatedItemsView: View) : RecyclerView.ViewHolder(TopRatedItemsView) {
        val tvPlayerName: TextView = TopRatedItemsView.findViewById(R.id.tv_player_name)
        val tvTimer: TextView = TopRatedItemsView.findViewById(R.id.tv_record_timer)
        val btnStartStop: Button = TopRatedItemsView.findViewById(R.id.btn_start_stop_player)
        val btnPauseResume: Button = TopRatedItemsView.findViewById(R.id.btn_pause_resume_player)



    }

    private fun play(audioPlayer: AudioPlayer, holder: ViewHolder) {

            audioPlayer.player.playFile(audioFile ,{
                stopPlayer(audioPlayer,holder)
            })
            holder.btnStartStop.text = "Reset"
            holder.btnPauseResume.apply {
                isEnabled = true
                text = "Pause"
            }
            audioPlayer.playerRunning = true
            holder.tvTimer.visibility = View.VISIBLE
            audioPlayer.timer.startTimer{ time ->
                holder.tvTimer.text = time
            }
    }



    private fun stopPlayer(audioPlayer: AudioPlayer, holder: ViewHolder) {
        audioPlayer.player.stop()
        holder.btnStartStop.text = "Start"
        holder.btnPauseResume.apply {
            isEnabled = false
            text = "Pause"
        }
        audioPlayer.playerRunning  = false
        audioPlayer.timer.stopTimer()
    }

    private fun pauseResumePlayer(audioPlayer: AudioPlayer, holder: ViewHolder){
        if(!audioPlayer.playerPause){
            audioPlayer.player.pause()
            audioPlayer.timer.pauseTimer()
            holder.btnPauseResume.text = "Resume"
            audioPlayer.playerPause = true
        }
        else{
            audioPlayer.player.resume()
            audioPlayer.timer.resumeTimer()
            holder.btnPauseResume.text = "Pause"
            audioPlayer.playerPause  = false
        }
    }

}