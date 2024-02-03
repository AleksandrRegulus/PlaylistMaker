package com.example.playlistmaker.ui.new_playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistsInteractor
import com.example.playlistmaker.domain.playlist.model.Playlist
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<NewPlaylistState>()
    val stateLiveData: LiveData<NewPlaylistState> = _stateLiveData

    private val _posterLiveData = MutableLiveData("")
    val posterLiveData: LiveData<String> = _posterLiveData

    fun setPoster(uri: String) {
        _posterLiveData.value = uri
    }

    fun createNewPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val result = playlistsInteractor.addPlaylist(playlist)
            if (result) {
                _stateLiveData.value = NewPlaylistState.PlaylistCreated(playlist.playlistName)
            } else {
                _stateLiveData.value = NewPlaylistState.Error(playlist.playlistName)
            }
        }
    }

}