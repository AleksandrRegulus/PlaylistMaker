package com.example.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val tracks = ArrayList<Track>()
    private val historyTracks = ArrayList<Track>()

    private val _stateLiveData = MutableLiveData<SearchTracksState>()
    val stateLiveData: LiveData<SearchTracksState> = _stateLiveData

    private fun renderState(state: SearchTracksState) {
        _stateLiveData.postValue(state)
    }

    private fun getHitsoryTracks() {
        renderState(SearchTracksState.Loading)
        historyTracks.clear()
        historyTracks.addAll(getSearchHistoryUseCase.execute())
        renderState(
            SearchTracksState.HistoryContent(historyTracks)
        )
    }

    fun saveToHistory(track: Track) {
        val position = historyTracks.indexOf(track)
        if (position == -1) {
            if (historyTracks.size == MAX_TRACKS_IN_HISTORY) historyTracks.removeLast()
        } else {
            historyTracks.removeAt(position)
        }
        historyTracks.add(0, element = track)
        saveSearchHistoryUseCase.execute(historyTracks)

        if (_stateLiveData.value is SearchTracksState.HistoryContent)
            _stateLiveData.value = SearchTracksState.HistoryContent(historyTracks)
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
            if (changedText.isEmpty()) getHitsoryTracks()
        }
    }

    fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchTracksState.Loading)

            viewModelScope.launch {
                searchTracksInteractor
                    .searchTracks(expression = newSearchText)
                    .collect { pair ->
                        val (foundTracks, errorMessage) = pair

                        if (foundTracks != null) {
                            tracks.clear()
                            tracks.addAll(foundTracks)
                        }
                        when {
                            errorMessage != null -> {
                                renderState(SearchTracksState.Error)
                            }

                            tracks.isEmpty() -> {
                                renderState(SearchTracksState.Empty)
                            }

                            else -> {
                                renderState(SearchTracksState.NetworkContent(tracks))
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