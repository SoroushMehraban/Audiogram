package com.reglardo.audiogram

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.reglardo.audiogram.databinding.ActivityMainBinding
import java.io.File
import com.google.gson.Gson
import com.reglardo.audiogram.adapter.TagAdapter
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    private var startTagTime = 0.0
    private var tagMap: MutableMap<Double, String> = mutableMapOf()
    private lateinit var runnable: Runnable
    private var handler = Handler()
    private val mediaViewModel: MediaViewModel by viewModels()

    companion object {
        fun getTimeStringFromDouble(time: Double): String {
            val resultInt = time.roundToInt()
            val hours = resultInt % 86400 / 3600
            val minutes = resultInt % 86400 % 3600 / 60
            val seconds = resultInt % 86400 % 3600 % 60

            return makeTimeString(hours, minutes, seconds)
        }


        private fun makeTimeString(hours: Int, minutes: Int, seconds: Int): String =
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

        val path = getFilePath()

        askAudioPermissionIfNotGranted()

        recordModeListener()
        playModeListener()
        editModeListener()

        startRecordingListener(path)
        stopRecordingListener()
        playRecordingListener(path)

        tagBtnListener()
        submitTagBtnListener()

        setRecordingState()
    }

    private fun setRecordingState() {
        if (mediaViewModel.isRecording) {
            binding.startRecordingBtn.isVisible = false
            binding.stopRecordingBtn.isVisible = true

            binding.tagBtn.isVisible = true
            binding.tagLayout.isVisible = false
        }
        else {
            binding.startRecordingBtn.isVisible = true
            binding.stopRecordingBtn.isVisible = false

            binding.tagBtn.isVisible = false
            binding.tagLayout.isVisible = false
        }
    }

    private fun recordModeListener() {
        binding.recordModeBtn.setOnClickListener { changeMode("RecordMode") }
        binding.recordModeBtnCaption.setOnClickListener { changeMode("RecordMode") }
    }

    private fun playModeListener() {
        binding.playModeBtn.setOnClickListener { changeMode("PlayMode") }
        binding.playModeBtnCaption.setOnClickListener { changeMode("PlayMode") }
    }

    private fun editModeListener() {
        binding.editModeBtn.setOnClickListener { changeMode("EditMode") }
        binding.editModeBtnCaption.setOnClickListener { changeMode("EditMode") }
    }

    private fun changeMode(mode: String) {
        // default colors
        var recordColor = R.color.primary_dark
        var playColor = R.color.primary_dark
        var editColor = R.color.primary_dark

        // default visibilities
        binding.recorderLayout.visibility = View.GONE
        binding.tagContainer.visibility = View.GONE
        binding.seekBar.visibility = View.INVISIBLE
        binding.playBtn.visibility = View.INVISIBLE

        if (mode == "RecordMode") {
            recordColor = R.color.secondary_light
            binding.recorderLayout.visibility = View.VISIBLE
        }
        if (mode == "PlayMode") {
            playColor = R.color.secondary_light

            binding.seekBar.visibility = View.VISIBLE
            binding.seekBar.isEnabled = false

            binding.playBtn.visibility = View.VISIBLE

            binding.tagContainer.visibility = View.VISIBLE
        }
        if (mode == "EditMode") {
            editColor = R.color.secondary_light
        }


        binding.recordModeBtn.setColorFilter(
            ContextCompat.getColor(
                applicationContext,
                recordColor
            ), PorterDuff.Mode.SRC_IN
        )
        binding.playModeBtn.setColorFilter(
            ContextCompat.getColor(
                applicationContext,
                playColor
            ), PorterDuff.Mode.SRC_IN
        )
        binding.editModeBtn.setColorFilter(
            ContextCompat.getColor(
                applicationContext,
                editColor
            ), PorterDuff.Mode.SRC_IN
        )
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
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
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


    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent)
        timerStarted = true
    }

    fun changePlayerTime(givenTime: Double) {
        stopTimer()
        time = givenTime
        startTimer()

        binding.seekBar.progress = (givenTime * 1000).toInt()
        mediaViewModel.mediaPlayer.seekTo((givenTime * 1000).toInt())
    }

    private fun stopTimer() {
        stopService(serviceIntent)
        timerStarted = true

        time = 0.0
        binding.timer.text = getTimeStringFromDouble(time)
    }



    private fun playRecordingListener(path: String) {
        binding.playBtn.setOnClickListener {
            binding.playBtn.isVisible = false
            binding.pauseBtn.isVisible = true

            mediaViewModel.mediaPlayer = MediaPlayer()
            mediaViewModel.mediaPlayer.setDataSource(path)
            mediaViewModel.mediaPlayer.prepare()

            binding.seekBar.isEnabled = true
            binding.seekBar.progress = 0
            binding.seekBar.max = mediaViewModel.mediaPlayer.duration
            binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2) {
                        mediaViewModel.mediaPlayer.seekTo(p1)

                        stopTimer()
                        time = p1.toDouble() / 1000
                        startTimer()
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            mediaViewModel.mediaPlayer.start()

            runnable = Runnable { // set progress of seekbar with time
                binding.seekBar.progress = mediaViewModel.mediaPlayer.currentPosition
                handler.postDelayed(runnable, 100)
            }

            startTimer()
            val mapStr = readRecordedTagMap()
            if(mapStr != "{}") {
                var tagMap = mutableMapOf<String, String>()
                tagMap = Gson().fromJson(mapStr, tagMap.javaClass)

                val recyclerView = binding.tagRecyclerView
                recyclerView.adapter = TagAdapter(this, tagMap.toList())
                recyclerView.setHasFixedSize(true)
            }

            handler.postDelayed(runnable, 100)
            mediaViewModel.mediaPlayer.setOnCompletionListener {
                stopTimer()

                binding.playBtn.isVisible = true
                binding.pauseBtn.isVisible = false

                binding.seekBar.isEnabled = false
            }
        }
    }

    private fun readRecordedTagMap(): String {
        val contextWrapper = ContextWrapper(applicationContext)
        val audioDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)


        val bufferedReader = File(audioDirectory, "testRecording.json").bufferedReader()
        val mapStr = bufferedReader.use { it.readText() }

        return mapStr
    }

    private fun stopRecordingListener() {
        binding.stopRecordingBtn.setOnClickListener {
            mediaViewModel.mediaRecorder.stop()
            mediaViewModel.changeIsRecordingState(false)

            binding.startRecordingBtn.isVisible = true
            binding.stopRecordingBtn.isVisible = false

            binding.tagBtn.isVisible = false
            binding.tagLayout.isVisible = false

            stopTimer()
            saveTagMap()
        }
    }

    private fun saveTagMap() {
        val gson = Gson()
        val tagMapStr = gson.toJson(tagMap)

        val contextWrapper = ContextWrapper(applicationContext)
        val audioDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        File(audioDirectory, "testRecording.json").writeText(tagMapStr)
    }

    private fun startRecordingListener(path: String) {
        binding.startRecordingBtn.setOnClickListener {
            mediaViewModel.mediaRecorder.setOutputFile(path)

            /// start recording
            mediaViewModel.mediaRecorder.prepare()
            mediaViewModel.mediaRecorder.start()
            mediaViewModel.changeIsRecordingState(true)

            binding.startRecordingBtn.isVisible = false
            binding.stopRecordingBtn.isVisible = true
            binding.tagBtn.isVisible = true

            startTimer()

            tagMap = mutableMapOf()
        }
    }

    private fun askAudioPermissionIfNotGranted() {
        val recordAudioNotGranted = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED

        if (recordAudioNotGranted) {
            binding.startRecordingBtn.isEnabled = false

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 111
            )
        } else {
            binding.startRecordingBtn.isEnabled = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            binding.startRecordingBtn.isEnabled = true
            binding.stopRecordingBtn.isEnabled = true
        }
    }

    private fun getFilePath(): String {
        val contextWrapper = ContextWrapper(applicationContext)
        val audioDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file = File(audioDirectory, "testRecording.mp3")
        return file.path
    }
}