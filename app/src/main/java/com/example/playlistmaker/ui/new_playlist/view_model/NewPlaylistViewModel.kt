package com.example.playlistmaker.ui.new_playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.playlist.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<NewPlaylistState>()
    val stateLiveData: LiveData<NewPlaylistState> = _stateLiveData

    fun createNewPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val result = playlistInteractor.addPlaylist(playlist)
            if (result) {
                _stateLiveData.value = NewPlaylistState.PlaylistCreated(playlist.playlistName)
            } else {
                _stateLiveData.value = NewPlaylistState.Error(playlist.playlistName)
            }
        }
    }

}