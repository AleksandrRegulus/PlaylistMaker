package com.example.playlistmaker.presentation.presenter

import com.example.playlistmaker.domain.model.Track

interface SearchTracksView {
    fun showTracks(arrayTracks: ArrayList<Track>)

    fun showProgressBar()

}