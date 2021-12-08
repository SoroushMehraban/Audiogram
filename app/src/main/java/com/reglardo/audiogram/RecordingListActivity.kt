package com.reglardo.audiogram

import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.reglardo.audiogram.adapter.RecordingAdapter
import com.reglardo.audiogram.adapter.TagAdapter
import com.reglardo.audiogram.databinding.ActivityRecordingListBinding
import java.io.File

class RecordingListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordingListBinding
    companion object {
        const val FROM = "from"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainActivityState = intent?.extras?.getString(FROM).toString()

        val listOfFiles = mutableListOf<String>()
        val contextWrapper = ContextWrapper(applicationContext)
        val audioDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        if (audioDirectory != null) {
            File(audioDirectory.path).walkTopDown().forEach {
                val fileName = it.path.split("/").last()
                if (fileName.contains(".mp3"))
                    listOfFiles.add(fileName)
            }
        }

        val recyclerView = binding.recordingRecyclerView
        recyclerView.adapter = RecordingAdapter(mainActivityState, listOfFiles)
    }
}