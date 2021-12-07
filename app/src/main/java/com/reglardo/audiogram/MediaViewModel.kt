package com.reglardo.audiogram

import android.media.MediaPlayer
import android.media.MediaRecorder
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
}