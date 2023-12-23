package com.example.playlistmaker.ui.player.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.FavoriteInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.DateTimeUtil.formatMillisToTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val mediaPlayer: MediaPlayer,
    private val favoriteInteractor: FavoriteInteractor,
) : ViewModel() {

    private var timerJob: Job? = null
    private var latestPreviewUrl = ""

    private val _stateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    val stateLiveData: LiveData<PlayerState> = _stateLiveData

    override fun onCleared() {
        releasePlayer()
        super.onCleared()
    }

    fun onFavoriteClicked(track: Track) {
        if (track.isFavorite) {
            viewModelScope.launch {
                favoriteInteractor.addTrackToFav(track)
            }
        } else {
            viewModelScope.launch {
                favoriteInteractor.deleteTrackFromFav(track.trackId)
            }
        }
    }

    private fun renderState(state: PlayerState) {
        _stateLiveData.postValue(state)
    }

    fun preparePlayer(previewUrl: String) {
        if (latestPreviewUrl != previewUrl) {
            latestPreviewUrl = previewUrl
            try {
                mediaPlayer.setDataSource(previewUrl)
                mediaPlayer.prepareAsync()

                mediaPlayer.setOnPreparedListener {
                    renderState(PlayerState.Prepared)
                }

                mediaPlayer.setOnCompletionListener {
                    timerJob?.cancel()
                    renderState(PlayerState.Prepared)
                }
            } catch (e: Throwable) {
                renderState(PlayerState.Default)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        renderState(PlayerState.Playing(formatMillisToTime(mediaPlayer.currentPosition.toLong())))
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(DELAY_MILLIS)
                renderState(PlayerState.Playing(formatMillisToTime(mediaPlayer.currentPosition.toLong())))
            }
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        renderState(PlayerState.Paused(formatMillisToTime(mediaPlayer.currentPosition.toLong())))
    }

    private fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        renderState(PlayerState.Default)
    }

    fun playbackControl() {
        when (_stateLiveData.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }

            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }

            else -> {}
        }
    }

    companion object {
        private const val DELAY_MILLIS = 300L
    }

}