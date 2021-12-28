package com.reglardo.audiogram.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.R
import com.reglardo.audiogram.RecordingListActivity
import com.reglardo.audiogram.fragments.RecorderFragment


class RecordingAdapter(
    private val mainActivityState: String,
    private val dataset: List<String>
) : RecyclerView.Adapter<RecordingAdapter.RecordingViewHolder>() {

    class RecordingViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val recordingName: TextView = view.findViewById(R.id.recording_name)
        val container: LinearLayout = view.findViewById(R.id.recording_name_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_recording, parent, false)

        return RecordingViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        val recording = dataset[position]

        holder.recordingName.text = recording
        holder.container.setOnClickListener {
            val context = holder.view.context
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(RecorderFragment.FILE, recording)
            intent.putExtra(RecordingListActivity.FROM, mainActivityState)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataset.size
}