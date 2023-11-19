package com.example.playlistmaker.ui.search.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.search.impl.SaveSearchHistoryUseCase
import com.example.playlistmaker.domain.search.model.Track

class SearchTracksViewModel(
    private val searchTracksInteractor: TracksInteractor,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val saveSearchHistoryUseCase: SaveSearchHistoryUseCase,
) : ViewModel() {

    private var latestSearchText: String? = null

    private val tracks = ArrayList<Track>()
    private val historyTracks = ArrayList<Track>()

    private val _stateLiveData = MutableLiveData<SearchTracksState>()
    val stateLiveData: LiveData<SearchTracksState> = _stateLiveData

    private val handler = Handler(Looper.getMainLooper())

    private fun renderState(state: SearchTracksState) {
        _stateLiveData.postValue(state)
    }

    fun getHitsoryTracks() {
        renderState(SearchTracksState.Loading)
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
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
        if (latestSearchText == changedText && _stateLiveData.value !is SearchTracksState.Error) {
            return
        }
        latestSearchText = changedText

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        if (changedText.isNotEmpty()) {
            val searchRunnable = Runnable { search(changedText) }
            val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY

            handler.postAtTime(
                searchRunnable,
                SEARCH_REQUEST_TOKEN,
                postTime,
            )
        }
    }

    fun cancelSearch() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun search(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchTracksState.Loading)

            searchTracksInteractor.searchTracks(
                expression = newSearchText,
                consumer = object : TracksInteractor.TracksConsumer {

                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        if (foundTracks != null) {
                            tracks.clear()
                            tracks.addAll(foundTracks)
                        }
                        when {
                            errorMessage != null -> {
                                renderState(
                                    SearchTracksState.Error
                                )
                            }

                            tracks.isEmpty() -> {
                                renderState(
                                    SearchTracksState.Empty
                                )
                            }

                            else -> {
                                renderState(
                                    SearchTracksState.NetworkContent(tracks)
                                )
                            }
                        }
                    }
                }
            )
        }
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val MAX_TRACKS_IN_HISTORY = 10
    }

}