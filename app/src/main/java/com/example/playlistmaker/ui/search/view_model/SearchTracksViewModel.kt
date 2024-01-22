package com.example.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.search.SearchResult
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.search.impl.SaveSearchHistoryUseCase
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class SearchTracksViewModel(
    private val searchTracksInteractor: TracksInteractor,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val saveSearchHistoryUseCase: SaveSearchHistoryUseCase,
) : ViewModel() {

    private var latestSearchText: String? = null
    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            search(changedText)
        }

    private val historyTracks = ArrayList<Track>()

    private val _stateLiveData = MutableLiveData<SearchTracksState>()
    val stateLiveData: LiveData<SearchTracksState> = _stateLiveData

    private fun renderState(state: SearchTracksState) {
        _stateLiveData.postValue(state)
    }

    fun actualTrackList() {
        when (_stateLiveData.value) {
            is SearchTracksState.HistoryContent -> getHistoryTracks()
            is SearchTracksState.NetworkContent -> latestSearchText?.let { search(it) }
            else -> {}
        }
    }

    private fun getHistoryTracks() {
        viewModelScope.launch {
            getSearchHistoryUseCase.execute().collect{ result ->
                historyTracks.clear()
                historyTracks.addAll(result)
                renderState(SearchTracksState.HistoryContent(historyTracks))
            }
        }
    }

    fun saveToHistory(track: Track) {
        val position = historyTracks.indexOfFirst { it.trackId == track.trackId }
        if (position == -1) {
            if (historyTracks.size == MAX_TRACKS_IN_HISTORY) historyTracks.removeLast()
        } else {
            historyTracks.removeAt(position)
        }
        historyTracks.add(0, element = track)
        saveSearchHistoryUseCase.execute(historyTracks)
    }

    fun clearHistory() {
        historyTracks.clear()
        saveSearchHistoryUseCase.execute(historyTracks)
        renderState(
            SearchTracksState.HistoryContent(historyTracks)
        )
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText || _stateLiveData.value is SearchTracksState.Error) {
            latestSearchText = changedText
            trackSearchDebounce(changedText)
            if (changedText.isEmpty()) getHistoryTracks()
        }
    }

    private fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchTracksState.Loading)

            viewModelScope.launch {
                searchTracksInteractor
                    .searchTracks(expression = newSearchText)
                    .collect { result ->
                        when (result) {
                            is SearchResult.Data -> {
                                val tracks = ArrayList<Track>()

                                if (result.tracks != null) {
                                    tracks.addAll(result.tracks)
                                }
                                when {
                                    tracks.isEmpty() -> {
                                        renderState(SearchTracksState.Empty)
                                    }

                                    else -> {
                                        renderState(SearchTracksState.NetworkContent(tracks))
                                    }
                                }
                            }

                            is SearchResult.Error -> {
                                renderState(SearchTracksState.Error)
                            }
                        }
                    }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val MAX_TRACKS_IN_HISTORY = 10
    }

}