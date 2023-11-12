package com.example.playlistmaker.ui.player.view_model

import android.media.MediaPlayer
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.util.DateTimeUtil.formatMillisToTime

class PlayerViewModel(
    previewUrl: String,
    private val mediaPlayer: MediaPlayer,
    private val handler: Handler,
) : ViewModel() {

    private val timerRunnable = object : Runnable {
        override fun run() {
            renderState(
                PlayerState.Playing(
                    formatMillisToTime(mediaPlayer.currentPosition.toLong())
                )
            )
            handler.postDelayed(this, DELAY_MILLIS)
        }
    }

    private val _stateLiveData = MutableLiveData<PlayerState>()
    val stateLiveData: LiveData<PlayerState> = _stateLiveData

    init {
        preparePlayer(previewUrl)
    }

    private fun renderState(state: PlayerState) {
        _stateLiveData.value = state
    }

    private fun preparePlayer(previewUrl: String) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            renderState(PlayerState.Prepared)
        }

        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(timerRunnable)
            renderState(PlayerState.Prepared)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        handler.post(timerRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        handler.removeCallbacks(timerRunnable)
        renderState(PlayerState.Paused)
    }

    fun playbackControl() {
        when (_stateLiveData.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }

            is PlayerState.Prepared, PlayerState.Paused -> {
                startPlayer()
            }

            else -> {}
        }
    }

    override fun onCleared() {
        handler.removeCallbacks(timerRunnable)
        mediaPlayer.release()
    }

    companion object {
        private const val DELAY_MILLIS = 300L
    }

}