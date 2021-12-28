package com.reglardo.audiogram

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.reglardo.audiogram.databinding.ActivityMainBinding
import java.io.File
import com.google.gson.Gson
import com.reglardo.audiogram.adapter.TagAdapter
import android.content.Intent
import android.text.InputType
import android.widget.EditText
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

import android.media.AudioManager


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    private var startTagTime = 0.0
    private var lastRecordingName = ""
    private var tagMap: MutableMap<Double, String> = mutableMapOf()
    private val mediaViewModel: MediaViewModel by viewModels()

    companion object {
        const val FILE = "file"

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

        askAudioPermissionIfNotGranted()

        // listeners for buttons above the page (Record, Play Recordings and trim)
        recordModeListener()
        playModeListener()
        trimModeListener()

        highPitchListener()
        lowPitchListener()
        normalPitchListener()

        startRecordingListener()
        stopRecordingListener()

        playAudioListener()
        pauseAudioListener()

        tagBtnListener()
        submitTagBtnListener()

        setRecordingState() // Useful for after rotation. Since current activity is destroyed and
                            // is created again, we need this function to enable stop recording
                            // button
        setPlayingState()   // Again for after rotation

        fileManagerListener()

        trimSaveListener()

        handleFileIntentIfExists() // When activity starts from RecordingListActivity

        binding.fileName.text = mediaViewModel.mediaFile
    }

    private fun trimSaveListener() {
        binding.saveTrim.setOnClickListener {
            val trimOutput = binding.trimOutput.text.toString()
            if (trimOutput == "") {
                showAlertDialog("Error", "Please enter output file name")
            }
            else {
                val contextWrapper = ContextWrapper(applicationContext)
                val audioDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                val file = File("$audioDirectory/$trimOutput.mp3")
                if (file.exists()) {
                    showAlertDialog("Error", "Recording with the same name exists. Try another one.")
                }
                else {
                    val startTime = (binding.trimSeekBar.selectedMinValue.toDouble() / 1000).toString()
                    val durationTime = ((binding.trimSeekBar.selectedMaxValue.toDouble() -
                                        binding.trimSeekBar.selectedMinValue.toDouble())
                                        / 1000).toString()

                    val command = "-ss $startTime " +
                                  "-i ${mediaViewModel.mediaPath} " +
                                  "-t $durationTime " +
                                  "$audioDirectory/$trimOutput.mp3"
                    FFmpeg.executeAsync(command) { _, returnCode ->
                        if (returnCode == RETURN_CODE_SUCCESS) {
                            mediaViewModel.setMediaFile("No file selected")
                            binding.trimSeekBar.isEnabled = false

                            binding.trimOutput.visibility = View.INVISIBLE
                            binding.saveTrim.visibility = View.INVISIBLE

                            binding.fileName.text = mediaViewModel.mediaFile

                            hideKeyboard(it)

                            showAlertDialog("Success", "Process finished")
                        }
                    }
                }
            }
        }
    }

    private fun handleFileIntentIfExists() {
        val selectedFile = intent?.extras?.getString(FILE)
        val previousState = intent?.extras?.getString(RecordingListActivity.FROM).toString()
        if (selectedFile != null) {
            val contextWrapper = ContextWrapper(applicationContext)
            val audioDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)

            val selectedPath = "$audioDirectory/$selectedFile"
            mediaViewModel.setMediaPath(selectedPath)
            mediaViewModel.setMediaFile(selectedFile)

            enableTrimSeekBar()

            changeMode(previousState)
        }
    }

    private fun enableTrimSeekBar() {
        binding.trimSeekBar.isEnabled = true

        binding.trimOutput.visibility = View.VISIBLE
        binding.saveTrim.visibility = View.VISIBLE

        mediaViewModel.mediaPlayer = MediaPlayer()
        mediaViewModel.mediaPlayer.setDataSource(mediaViewModel.mediaPath)
        mediaViewModel.mediaPlayer.prepare()

        binding.trimEnd.text = getTimeStringFromDouble((mediaViewModel.mediaPlayer.duration / 1000).toDouble())

        binding.trimSeekBar.setRangeValues(0, mediaViewModel.mediaPlayer.duration)
        binding.trimSeekBar.selectedMinValue = 0
        binding.trimSeekBar.selectedMaxValue = mediaViewModel.mediaPlayer.duration

        binding.trimSeekBar.setOnRangeSeekBarChangeListener { bar, minValue, maxValue ->
            binding.trimStart.text = getTimeStringFromDouble(minValue.toString().toDouble() / 1000)
            binding.trimEnd.text = getTimeStringFromDouble(maxValue.toString().toDouble() / 1000)
        }
    }

    private fun fileManagerListener() {
        binding.fileManagerBtn.setOnClickListener {
            openRecordingList()
        }
    }

    private fun openRecordingList() {
        val intent = Intent(this, RecordingListActivity::class.java)
        if (binding.trimSeekBar.isVisible) { // open trim section after come back
            intent.putExtra(RecordingListActivity.FROM, "trimMode")
        }
        else { // open play section after come back
            intent.putExtra(RecordingListActivity.FROM, "PlayMode")
        }
        startActivity(intent)
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

    private fun setPlayingState() {
        if (mediaViewModel.isPlaying) {
            changeMode("PlayMode")

            binding.seekBar.isEnabled = true
            initializeSeekbar()
            updateTagContainer()

            Thread {
                while(mediaViewModel.mediaPlayer.isPlaying) {
                    binding.seekBar.progress = mediaViewModel.mediaPlayer.currentPosition
                    Thread.sleep(100)
                }
            }.start()
        }
    }

    private fun recordModeListener() {
        binding.recordModeBtn.setOnClickListener { changeMode("RecordMode") }
        binding.recordModeBtnCaption.setOnClickListener { changeMode("RecordMode") }
    }

    private fun highPitchListener() {
        binding.highPitchBtn.setOnClickListener {
            if (!mediaViewModel.mediaPlayer.isPlaying) {
                mediaViewModel.params.pitch = 1.5f

                binding.highPitchBtn.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.secondary_light
                    ), PorterDuff.Mode.SRC_IN
                )

                binding.normalPitchBtn.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.primary_dark
                    ), PorterDuff.Mode.SRC_IN
                )

                binding.lowPitchBtn.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.primary_dark
                    ), PorterDuff.Mode.SRC_IN
                )
            }
        }
    }
    private fun lowPitchListener() {
        binding.lowPitchBtn.setOnClickListener {
            if (!mediaViewModel.mediaPlayer.isPlaying) {
                mediaViewModel.params.pitch = 0.75f

                binding.highPitchBtn.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.primary_dark
                    ), PorterDuff.Mode.SRC_IN
                )

                binding.normalPitchBtn.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.primary_dark
                    ), PorterDuff.Mode.SRC_IN
                )

                binding.lowPitchBtn.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.secondary_light
                    ), PorterDuff.Mode.SRC_IN
                )
            }
        }
    }
    private fun normalPitchListener() {
        binding.normalPitchBtn.setOnClickListener {
            if (!mediaViewModel.mediaPlayer.isPlaying) {
                mediaViewModel.params.pitch = 1f

                binding.highPitchBtn.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.primary_dark
                    ), PorterDuff.Mode.SRC_IN
                )

                binding.normalPitchBtn.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.secondary_light
                    ), PorterDuff.Mode.SRC_IN
                )

                binding.lowPitchBtn.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.primary_dark
                    ), PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    private fun playModeListener() {
        binding.playModeBtn.setOnClickListener { changeMode("PlayMode") }
        binding.playModeBtnCaption.setOnClickListener { changeMode("PlayMode") }
    }

    private fun trimModeListener() {
        binding.trimModeBtn.setOnClickListener { changeMode("trimMode") }
        binding.trimModeBtnCaption.setOnClickListener { changeMode("trimMode") }
    }

    private fun changeMode(mode: String) {
        // default colors
        var recordColor = R.color.primary_dark
        var playColor = R.color.primary_dark
        var trimColor = R.color.primary_dark

        // default visibilities
        binding.timer.visibility = View.VISIBLE
        binding.recorderLayout.visibility = View.GONE
        binding.tagContainer.visibility = View.GONE
        binding.seekBar.visibility = View.INVISIBLE
        binding.playBtn.visibility = View.INVISIBLE
        binding.highPitchBtn.visibility = View.INVISIBLE
        binding.lowPitchBtn.visibility = View.INVISIBLE
        binding.normalPitchBtn.visibility = View.INVISIBLE
        binding.pauseBtn.visibility = View.INVISIBLE
        binding.fileManagerBtn.visibility = View.INVISIBLE
        binding.fileName.visibility = View.INVISIBLE

        binding.trimSeekBar.visibility = View.INVISIBLE
        binding.trimStart.visibility = View.INVISIBLE
        binding.trimEnd.visibility = View.INVISIBLE
        binding.trimOutput.visibility = View.INVISIBLE
        binding.saveTrim.visibility = View.INVISIBLE

        if (mode == "RecordMode") {
            changeTimerLocation("top")

            recordColor = R.color.secondary_light
            binding.recorderLayout.visibility = View.VISIBLE
            stopRecordingPlayer()  // in case if it is playing an audio
        }
        if (mode == "PlayMode") {
            changeTimerLocation("bottom")

            playColor = R.color.secondary_light

            binding.seekBar.visibility = View.VISIBLE
            binding.seekBar.isEnabled = false

            binding.playBtn.visibility = View.VISIBLE
            binding.highPitchBtn.visibility = View.VISIBLE
            binding.lowPitchBtn.visibility = View.VISIBLE
            binding.normalPitchBtn.visibility = View.VISIBLE
            binding.fileManagerBtn.visibility = View.VISIBLE

            binding.tagContainer.visibility = View.VISIBLE
            binding.fileName.visibility = View.VISIBLE
        }
        if (mode == "trimMode") {
            trimColor = R.color.secondary_light
            stopRecordingPlayer()  // in case if it is playing an audio
            binding.timer.visibility = View.INVISIBLE

            binding.trimSeekBar.visibility = View.VISIBLE
            binding.trimStart.visibility = View.VISIBLE
            binding.trimEnd.visibility = View.VISIBLE

            binding.fileManagerBtn.visibility = View.VISIBLE
            binding.fileName.visibility = View.VISIBLE

            if (mediaViewModel.mediaFile == "No file selected")
                binding.trimSeekBar.isEnabled = false
            else {
                binding.trimOutput.visibility = View.VISIBLE
                binding.saveTrim.visibility = View.VISIBLE
            }
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
        binding.trimModeBtn.setColorFilter(
            ContextCompat.getColor(
                applicationContext,
                trimColor
            ), PorterDuff.Mode.SRC_IN
        )
    }

    private fun changeTimerLocation(location: String) {
        if (location == "bottom") {
            val params = binding.timer.layoutParams as ConstraintLayout.LayoutParams
            params.topToBottom = ConstraintLayout.LayoutParams.UNSET
            params.bottomToTop = R.id.navigation_bar
            params.startToStart = ConstraintLayout.LayoutParams.UNSET
            params.rightMargin = 100
            binding.timer.layoutParams = params
            binding.timer.requestLayout()
        }
        if (location == "top") {
            val params = binding.timer.layoutParams as ConstraintLayout.LayoutParams
            params.topToBottom = R.id.play_mode_btn_caption
            params.bottomToTop = ConstraintLayout.LayoutParams.UNSET
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            params.rightMargin = 0
            binding.timer.layoutParams = params
            binding.timer.requestLayout()
        }
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

        time = 0.0
        binding.timer.text = getTimeStringFromDouble(time)
    }

    private fun pauseTimer() {
        stopService(serviceIntent)
    }


    private fun playAudioListener() {
        binding.playBtn.setOnClickListener {
            if (!mediaViewModel.isPlaying) { // playing for the first time
                if (mediaViewModel.mediaPath == "") {
                    openRecordingList()
                    return@setOnClickListener
                }
                mediaViewModel.mediaPlayer = MediaPlayer()
                mediaViewModel.mediaPlayer.setDataSource(mediaViewModel.mediaPath)
                mediaViewModel.mediaPlayer.prepare()


                initializeSeekbar()
                mediaViewModel.changeIsPlayingState(true)
                updateTagContainer()

                mediaViewModel.mediaPlayer.setOnCompletionListener {
                    stopTimer()
                    makeTagListEmpty()
                    mediaViewModel.changeIsPlayingState(false)

                    binding.playBtn.isVisible = true
                    binding.pauseBtn.isVisible = false
                    changePitchConstraints("afterComplete")

                    binding.seekBar.isEnabled = false
                    binding.seekBar.progress = 0
                }

            }
            binding.playBtn.isVisible = false
            binding.pauseBtn.isVisible = true
            changePitchConstraints("afterPlay")

            binding.seekBar.isEnabled = true
            mediaViewModel.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaViewModel.mediaPlayer.playbackParams = mediaViewModel.params
            mediaViewModel.mediaPlayer.start()
            Thread {
                while(mediaViewModel.mediaPlayer.isPlaying) {
                    binding.seekBar.progress = mediaViewModel.mediaPlayer.currentPosition
                    Thread.sleep(100)
                }
            }.start()
            startTimer()
        }
    }

    private fun changePitchConstraints(state: String) {
        if (state == "afterPlay") {
            val lowPitchParams = binding.lowPitchBtn.layoutParams as ConstraintLayout.LayoutParams
            lowPitchParams.bottomToTop = R.id.pause_btn
            binding.lowPitchBtn.layoutParams = lowPitchParams
            binding.lowPitchBtn.requestLayout()

            val normalPitchParams = binding.normalPitchBtn.layoutParams as ConstraintLayout.LayoutParams
            normalPitchParams.bottomToTop = R.id.pause_btn
            binding.normalPitchBtn.layoutParams = normalPitchParams
            binding.normalPitchBtn.requestLayout()

            val highPitchParams = binding.highPitchBtn.layoutParams as ConstraintLayout.LayoutParams
            highPitchParams.bottomToTop = R.id.pause_btn
            binding.highPitchBtn.layoutParams = highPitchParams
            binding.highPitchBtn.requestLayout()
        }
        else {
            val lowPitchParams = binding.lowPitchBtn.layoutParams as ConstraintLayout.LayoutParams
            lowPitchParams.bottomToTop = R.id.play_btn
            binding.lowPitchBtn.layoutParams = lowPitchParams
            binding.lowPitchBtn.requestLayout()

            val normalPitchParams = binding.normalPitchBtn.layoutParams as ConstraintLayout.LayoutParams
            normalPitchParams.bottomToTop = R.id.play_btn
            binding.normalPitchBtn.layoutParams = normalPitchParams
            binding.normalPitchBtn.requestLayout()

            val highPitchParams = binding.highPitchBtn.layoutParams as ConstraintLayout.LayoutParams
            highPitchParams.bottomToTop = R.id.play_btn
            binding.highPitchBtn.layoutParams = highPitchParams
            binding.highPitchBtn.requestLayout()
        }
    }

    private fun updateTagContainer() {
        val mapStr = readRecordedTagMap()
        if (mapStr != "{}") {
            var tagMap = mutableMapOf<String, String>()
            tagMap = Gson().fromJson(mapStr, tagMap.javaClass)

            val recyclerView = binding.tagRecyclerView
            recyclerView.adapter = TagAdapter(this, tagMap.toList())
            recyclerView.setHasFixedSize(true)
        }
    }

    private fun initializeSeekbar() {
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
    }

    private fun pauseAudioListener() {
        binding.pauseBtn.setOnClickListener {
            binding.playBtn.isVisible = true
            binding.pauseBtn.isVisible = false
            changePitchConstraints("afterPause")

            mediaViewModel.mediaPlayer.pause()
            pauseTimer()
        }
    }


    private fun stopRecordingPlayer() {
        mediaViewModel.mediaPlayer.stop()

        binding.seekBar.isEnabled = false
        binding.seekBar.progress = 0

        makeTagListEmpty()
        stopTimer()
    }

    private fun makeTagListEmpty() {
        binding.tagRecyclerView.adapter = TagAdapter(this, listOf())
    }

    private fun readRecordedTagMap(): String {
        var mapStr = "{}"

        val tagFile = File(mediaViewModel.mediaPath.replace(".mp3", ".json"))
        if (tagFile.exists()) {
            val bufferedReader = tagFile.bufferedReader()
            mapStr = bufferedReader.use { it.readText() }
        }

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

            showGetRecordingNameDialog("Enter voice title")
        }
    }

    private fun saveTagMap(tagName: String) {
        val gson = Gson()
        val tagMapStr = gson.toJson(tagMap)

        val contextWrapper = ContextWrapper(applicationContext)
        val audioDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        File(audioDirectory, "$tagName.json").writeText(tagMapStr)
    }

    private fun startRecordingListener() {

        binding.startRecordingBtn.setOnClickListener {
            mediaViewModel.mediaRecorder.setOutputFile(makePathByCurrentDate())

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

    private fun makePathByCurrentDate(): String {
        val sdf = SimpleDateFormat("dd-M-yyyy hh:mm:ss")
        lastRecordingName = sdf.format(Date())

        val contextWrapper = ContextWrapper(applicationContext)
        val audioDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val path = "$audioDirectory/$lastRecordingName.mp3"
        return path
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

    private fun showGetRecordingNameDialog(title: String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)

        val input = EditText(this)
        input.hint = "Enter name"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Save", DialogInterface.OnClickListener { dialog, which ->
            var givenName = input.text.toString()

            val contextWrapper = ContextWrapper(applicationContext)
            val audioDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)

            val toFile = File("$audioDirectory/$givenName.mp3")
            if (toFile.exists()) {
                showGetRecordingNameDialog("Try another name")
            }
            else {
                val fromFile = File("$audioDirectory/$lastRecordingName.mp3")
                fromFile.renameTo(toFile)

                saveTagMap(givenName)
            }
        })

        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun showAlertDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton("OK", null)
            .show()
    }
}