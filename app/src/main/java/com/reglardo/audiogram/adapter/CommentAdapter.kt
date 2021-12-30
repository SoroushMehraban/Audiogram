package com.reglardo.audiogram.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android.marsphotos.network.URL
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.MediaViewModel
import com.reglardo.audiogram.R
import com.reglardo.audiogram.fragments.RecorderFragment
import com.reglardo.audiogram.network.VoiceCommentResponse
import org.w3c.dom.Comment


class CommentAdapter(
    private val comments: List<VoiceCommentResponse>,
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val userImage: ImageView = view.findViewById(R.id.comment_user_image)
        val username: TextView = view.findViewById(R.id.comment_username)
        val commentDate: TextView = view.findViewById(R.id.comment_date)
        val commentContent: TextView = view.findViewById(R.id.comment_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_comment, parent, false)

        return CommentViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        holder.userImage.load("${URL.BASE_URL}${comment.userImage}")
        holder.username.text = comment.username
        holder.commentDate.text = comment.commentDate
        holder.commentContent.text = comment.content
    }

    override fun getItemCount() = comments.size
}