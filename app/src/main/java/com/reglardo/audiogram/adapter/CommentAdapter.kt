package com.reglardo.audiogram.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android.marsphotos.network.URL
import com.reglardo.audiogram.R
import com.reglardo.audiogram.network.VoiceCommentResponse


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