package com.example.playlistmaker.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistsInteractor
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.DateTimeUtil
import com.example.playlistmaker.util.StringUtil
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private var currentPlaylist: Playlist? = null

    private val _playlistLiveData = MutableLiveData<PlaylistState>()
    val playlistLiveData: LiveData<PlaylistState> = _playlistLiveData

    private val _playlistDeleted = MutableLiveData(false)
    val playlistDeleted: LiveData<Boolean> = _playlistDeleted

    fun getPlaylist(playlistId: Int) {
        viewModelScope.launch {

            playlistsInteractor.getPlaylist(playlistId).collect { result ->
                currentPlaylist = result
            }

            if (currentPlaylist != null) {
                playlistsInteractor.getPlaylistTracks(currentPlaylist!!.trackIDs).collect { tracksList ->
                    _playlistLiveData.postValue(
                        PlaylistState(
                            playlist = currentPlaylist!!,
                            tracks = tracksList,
                            tracksDuration = getTracksDurationSum(tracksList),
                            numTracks = "${currentPlaylist!!.numberOfTracks} ${
                                StringUtil.getNumTracksString(
                                    currentPlaylist!!.numberOfTracks
                                )
                            }"
                        )
                    )
                }
            }
        }
    }

    fun deleteTrackFromPlaylist(trackId: Int){
        viewModelScope.launch {
            playlistsInteractor.deleteTrackFromPlaylist(currentPlaylist!!, trackId)
            getPlaylist(currentPlaylist!!.playlistId)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            if (playlistsInteractor.deletePlaylist(currentPlaylist!!)) _playlistDeleted.value = true
        }
    }

    private fun getTracksDurationSum(tracksList: List<Track>): String {
        var durationSum: Long = 0
        tracksList.forEach {
            durationSum += it.trackTimeMillis
        }

        val minutes = DateTimeUtil.formatMillisToMinutes(durationSum)

        return "$minutes ${StringUtil.getMinutesString(minutes.toInt())}"
    }
}