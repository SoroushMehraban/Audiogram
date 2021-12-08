package com.reglardo.audiogram

import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MediaViewModel: ViewModel() {
    var mediaPlayer = MediaPlayer()
    val mediaRecorder = MediaRecorder()

    private var _isRecording = false
    val isRecording: Boolean
        get() =_isRecording

    private var _isPlaying = false
    val isPlaying: Boolean
        get() =_isPlaying

    private var _mediaPath = ""
    val mediaPath: String
        get() = _mediaPath

    private var _mediaFile = "No file selected"
    val mediaFile: String
        get() = _mediaFile

    init {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB) // for optimizing the speech encoding
    }


    fun changeIsRecordingState(state: Boolean) {
        _isRecording = state
    }

    fun changeIsPlayingState(state: Boolean) {
        _isPlaying = state
    }

    fun setMediaPath(path: String) {
        _mediaPath = path
    }

    fun setMediaFile(path: String) {
        _mediaFile = path
    }
}