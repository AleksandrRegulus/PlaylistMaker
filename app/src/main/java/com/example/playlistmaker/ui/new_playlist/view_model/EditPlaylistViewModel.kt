package com.example.playlistmaker.ui.new_playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistsInteractor
import com.example.playlistmaker.domain.playlist.model.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : NewPlaylistViewModel(playlistsInteractor) {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    private val _playlistUpdated = MutableLiveData(false)
    val playlistUpdated: LiveData<Boolean> = _playlistUpdated

    fun getPlaylist(plalistId: Int) {
        viewModelScope.launch {
            playlistsInteractor.getPlaylist(plalistId).collect { result ->
                _playlist.postValue(result)
            }
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            _playlistUpdated.postValue(playlistsInteractor.updatePlaylist(playlist))
        }
    }
}