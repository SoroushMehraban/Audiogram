package com.reglardo.audiogram.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android.marsphotos.network.URL
import com.reglardo.audiogram.R
import com.reglardo.audiogram.network.UserVoiceResponse
import com.reglardo.audiogram.network.VoiceResponse


class VoiceAdapter(
    private val voiceResponse: List<UserVoiceResponse>
) : RecyclerView.Adapter<VoiceAdapter.VoiceViewHolder>() {

    class VoiceViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val userImage: ImageView = view.findViewById(R.id.voice_user_image)
        val username: TextView = view.findViewById(R.id.voice_user_username)
        val publishDate: TextView = view.findViewById(R.id.voice_publish_date)

        val voiceSeekbar: SeekBar = view.findViewById(R.id.voice_seekbar)
        val playPauseBtn: ImageButton = view.findViewById(R.id.play_pause_btn)

        val commentLayout: LinearLayout = view.findViewById(R.id.comment_layout)
        val commentNumber: TextView = view.findViewById(R.id.comment_number)

        val likeLayout: LinearLayout = view.findViewById(R.id.like_layout)
        val likeImg: ImageView = view.findViewById(R.id.like_img)
        val likeNumber: TextView = view.findViewById(R.id.like_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_voice, parent, false)

        return VoiceViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: VoiceViewHolder, position: Int) {
        val voiceResponse = voiceResponse[position]

        holder.userImage.load("${URL.BASE_URL}${voiceResponse.imageUrl}")
        holder.username.text = voiceResponse.username
        holder.publishDate.text = voiceResponse.publishDate
        holder.likeNumber.text = voiceResponse.likeNumbers.toString()
        holder.commentNumber.text = voiceResponse.commentNumbers.toString()
    }

    override fun getItemCount() = voiceResponse.size
}