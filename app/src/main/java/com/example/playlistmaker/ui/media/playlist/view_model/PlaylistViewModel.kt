package com.example.playlistmaker.ui.media.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private val _stateLiveData = MutableLiveData<PlaylistsState>()
    val stateLiveData: LiveData<PlaylistsState> = _stateLiveData

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect{result ->
                if (result.isEmpty()) _stateLiveData.postValue(PlaylistsState.Empty)
                else _stateLiveData.postValue(PlaylistsState.PlaylistContent(result))
            }
        }
    }
}