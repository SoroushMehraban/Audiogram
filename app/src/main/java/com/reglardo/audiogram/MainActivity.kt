package com.reglardo.audiogram

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.reglardo.audiogram.databinding.ActivityMainBinding
import java.io.File
import com.google.gson.Gson
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaRecorder: MediaRecorder
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    private var startTagTime = 0.0
    private lateinit var tagMap: MutableMap<Double, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

        mediaRecorder = MediaRecorder()

        val path = getFilePath()

        askAudioPermissionIfNotGranted()

        startRecordingListener(path)
        stopRecordingListener()
        playRecordingListener(path)

        tagBtnListener()
        submitTagBtnListener()
    }

    private fun submitTagBtnListener() {
        binding.submitTagBtn.setOnClickListener {
            val tagValue = binding.tagTextEditText.text.toString()
            tagMap.put(startTagTime, tagValue)
            binding.tagTextEditText.setText("")

            binding.tagBtn.isVisible = true
            binding.tagLayout.isVisible = false

            hideKeyboard(it)
        }
    }


    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun tagBtnListener() {
        binding.tagBtn.setOnClickListener {
            binding.tagBtn.isVisible = false
            binding.tagLayout.isVisible = true

            startTagTime = time
        }
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            binding.timer.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds  = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Int, minutes: Int, seconds: Int): String = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent)
        timerStarted = true
    }

    private fun stopTimer() {
        stopService(serviceIntent)
        timerStarted = true

        time = 0.0
        binding.timer.text = getTimeStringFromDouble(time)
    }

    private fun playRecordingListener(path: String) {
        binding.playBtn.setOnClickListener {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(path)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }
    }

    private fun stopRecordingListener() {
        binding.stopBtn.setOnClickListener {
            mediaRecorder.stop()
            binding.startBtn.isVisible = true
            binding.stopBtn.isVisible = false
            binding.playBtn.isEnabled = true

            binding.tagBtn.isVisible = false
            binding.tagLayout.isVisible = false
            Toast.makeText(applicationContext, "${tagMap}", Toast.LENGTH_LONG).show()

            stopTimer()
            saveTagMap()
        }
    }

    private fun saveTagMap() {
        if (tagMap.isNotEmpty()) {
            val gson = Gson()
            val tagMapStr = gson.toJson(tagMap)

            val contextWrapper = ContextWrapper(applicationContext)
            val audioDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            File(audioDirectory, "testRecording.json").writeText(tagMapStr)
        }
    }

    private fun startRecordingListener(path: String) {
        binding.startBtn.setOnClickListener {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB) // for optimizing the speech encoding
            mediaRecorder.setOutputFile(path)

            /// start recording
            mediaRecorder.prepare()
            mediaRecorder.start()

            binding.startBtn.isVisible = false
            binding.stopBtn.isVisible = true
            binding.playBtn.isEnabled = false
            binding.tagBtn.isVisible = true

            startTimer()

            tagMap = mutableMapOf()
        }
    }

    private fun askAudioPermissionIfNotGranted() {
        val recordAudioNotGranted = ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED

        if (recordAudioNotGranted) {
            binding.startBtn.isEnabled = false

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 111
            )
        } else {
            binding.startBtn.isEnabled = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            binding.startBtn.isEnabled = true
            binding.stopBtn.isEnabled = true
        }
    }

    private fun getFilePath(): String {
        val contextWrapper = ContextWrapper(applicationContext)
        val audioDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file = File(audioDirectory, "testRecording.mp3")
        Toast.makeText(applicationContext, file.path, Toast.LENGTH_LONG).show()
        return file.path
    }
}