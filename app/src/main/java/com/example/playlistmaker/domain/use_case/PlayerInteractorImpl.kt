package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository

class PlayerInteractorImpl(private val repository: PlayerRepository): PlayerInteractor {
    override fun preparePlayer(previewUrl: String) {
        repository.preparePlayer(previewUrl)
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun playbackControl() {
        repository.playbackControl()
    }

    override fun release() {
        repository.release()
    }

    override fun getState(): Int {
        return repository.getState()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }
}