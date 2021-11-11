package com.reglardo.audiogram.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.R


class TagAdapter(
    private val context: Context,
    private val dataset: List<Pair<String, String>>
    ) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    class TagViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val tagValue: TextView = view.findViewById(R.id.tag_value)
        val tagTime: TextView = view.findViewById(R.id.tag_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_tag, parent, false)

        return TagViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = dataset[position]
        holder.tagTime.text = MainActivity.getTimeStringFromDouble(tag.first.toDouble())
        holder.tagValue.text = tag.second
    }

    override fun getItemCount() = dataset.size
}