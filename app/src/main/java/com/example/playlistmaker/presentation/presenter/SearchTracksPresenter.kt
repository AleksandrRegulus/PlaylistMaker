package com.example.playlistmaker.presentation.presenter

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.model.Track

class SearchTracksPresenter(
    private val view: SearchTracksView
) {

    private val searchTracksInteractor = Creator.provideTracksInteractor()
    private var currentConsumeRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())

    private fun getConsumeRunnable(foundTracks: List<Track>): Runnable {
        return Runnable {
            view.showTracks(ArrayList(foundTracks))
        }
    }

    fun search(searchText: String) {
        view.showProgressBar()

        searchTracksInteractor.searchTracks(
            expression = searchText,
            consumer = object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                    handler.removeCallbacksSafe(currentConsumeRunnable)

                    val consumeRunnable = getConsumeRunnable(foundTracks)
                    currentConsumeRunnable = consumeRunnable

                    handler.post(consumeRunnable)
                }
            }
        )
    }

    fun onDestroy() {
        handler.removeCallbacksSafe(currentConsumeRunnable)
    }

    private fun Handler.removeCallbacksSafe(r: Runnable?) {
        r?.let {
            removeCallbacks(r)
        }
    }
}