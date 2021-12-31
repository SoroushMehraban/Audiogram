package com.reglardo.audiogram.adapter

import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android.marsphotos.network.URL
import com.example.android.marsphotos.network.VoiceApi
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.R
import com.reglardo.audiogram.fragments.CommentFragment
import com.reglardo.audiogram.network.UserVoiceResponse
import kotlinx.coroutines.*
import java.lang.Exception


class VoiceAdapter(
    private val currentFragment: Fragment,
    private val voiceResponse: List<UserVoiceResponse>,
    private val fromFragment: String
) : RecyclerView.Adapter<VoiceAdapter.VoiceViewHolder>() {

    class VoiceViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val userImage: ImageView = view.findViewById(R.id.voice_user_image)
        val username: TextView = view.findViewById(R.id.voice_user_username)
        val publishDate: TextView = view.findViewById(R.id.voice_publish_date)

        val voiceSeekbar: SeekBar = view.findViewById(R.id.voice_seekbar)
        val playPauseBtn: ImageButton = view.findViewById(R.id.play_pause_btn)

        val commentImg: ImageView = view.findViewById(R.id.comment_img)
        val commentNumber: TextView = view.findViewById(R.id.comment_number)

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

        holder.voiceSeekbar.isEnabled = false

        holder.userImage.load("${URL.BASE_URL}${voiceResponse.imageUrl}")
        holder.username.text = voiceResponse.username
        holder.publishDate.text = voiceResponse.publishDate
        holder.likeNumber.text = voiceResponse.likeNumbers.toString()
        holder.commentNumber.text = voiceResponse.commentNumbers.toString()

        var isLiked = false
        if (voiceResponse.isLiked) {
            holder.likeImg.setImageResource(R.drawable.heart_active)
            isLiked = true
        }

        holder.likeImg.setOnClickListener {
            GlobalScope.launch {
                try {
                    val response = VoiceApi.retrofitService.like(MainActivity.token, voiceResponse.id)
                    response.let {
                        if (it.success) {
                            withContext(Dispatchers.Main) {
                                if (isLiked) {
                                    isLiked = false
                                    holder.likeImg.setImageResource(R.drawable.heart)
                                }
                                else {
                                    isLiked = true
                                    holder.likeImg.setImageResource(R.drawable.heart_active)
                                }
                                holder.likeNumber.text = it.message
                            }
                        }
                    }
                } catch (e: Exception) {}
            }
        }

        holder.commentImg.setOnClickListener {
            replaceFragment(CommentFragment.newInstance(voiceResponse.id))
        }



        var isPlaying = false
        val mediaPlayer =  MediaPlayer.create(holder.view.context,
            Uri.parse("${URL.BASE_URL}${voiceResponse.voiceUrl}"))

        holder.playPauseBtn.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.pause()
                isPlaying = false
                holder.playPauseBtn.setImageResource(R.drawable.ic_play)
            }
            else {
                mediaPlayer.start()

                isPlaying = true
                holder.playPauseBtn.setImageResource(R.drawable.ic_pause)

                mediaPlayer.setOnCompletionListener {
                    isPlaying = false
                    holder.playPauseBtn.setImageResource(R.drawable.ic_play)
                    holder.voiceSeekbar.isEnabled = false
                    holder.voiceSeekbar.progress = 0
                }

                holder.voiceSeekbar.isEnabled = true
                holder.voiceSeekbar.max = mediaPlayer.duration
                Thread {
                    while (mediaPlayer.isPlaying) {
                        holder.voiceSeekbar.progress = mediaPlayer.currentPosition
                        Thread.sleep(100)
                    }
                }.start()

                holder.voiceSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        if (p2) {
                            mediaPlayer.seekTo(p1)
                        }
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {}
                    override fun onStopTrackingTouch(p0: SeekBar?) {}
                })
            }
        }
    }

    override fun getItemCount() = voiceResponse.size

    private fun replaceFragment(fragment: Fragment) {
        val transaction = currentFragment.parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment, fromFragment)
        transaction.commit()
    }
}