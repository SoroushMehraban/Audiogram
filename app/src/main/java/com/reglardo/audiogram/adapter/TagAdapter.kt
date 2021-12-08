package com.reglardo.audiogram.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.MediaViewModel
import com.reglardo.audiogram.R


class TagAdapter(
    private val context: Context,
    private val dataset: List<Pair<String, String>>
    ) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    class TagViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val tagCard: LinearLayout = view.findViewById(R.id.tag_card)
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
        holder.tagCard.setOnClickListener {
            (context as MainActivity).changePlayerTime(tag.first.toDouble())
        }
    }

    override fun getItemCount() = dataset.size
}