package com.reglardo.audiogram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.reglardo.audiogram.adapter.TagAdapter

class TagListActivity : AppCompatActivity() {
    companion object {
        const val MAP_CONTENT = "MAP_CONTENT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_list)

        val mapStr = intent?.extras?.getString(MAP_CONTENT).toString()

        var tagMap = mutableMapOf<String, String>()
        tagMap = Gson().fromJson(mapStr, tagMap.javaClass)

        val recyclerView = findViewById<RecyclerView>(R.id.tag_recycler_view)
        recyclerView.adapter = TagAdapter(this, tagMap.toList())
        recyclerView.setHasFixedSize(true)
    }
}