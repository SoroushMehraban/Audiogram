package com.reglardo.audiogram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class TagList : AppCompatActivity() {
    companion object {
        const val MAP_CONTENT = "MAP_CONTENT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_list)

        val mapStr = intent?.extras?.getString(MAP_CONTENT).toString()
        Toast.makeText(applicationContext, mapStr, Toast.LENGTH_LONG).show()
    }
}