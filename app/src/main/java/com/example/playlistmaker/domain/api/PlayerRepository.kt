package com.example.playlistmaker.domain.api

interface PlayerRepository {

    fun preparePlayer(previewUrl: String)
    fun pausePlayer()
    fun playbackControl()
    fun release()
    fun getState(): Int
    fun getCurrentPosition(): Int
}