package com.example.playlistmaker.ui.player.view_model

sealed interface PlayerState {

    object Prepared: PlayerState
    object Paused: PlayerState
    data class Playing(
        val currentPosition: String
    ): PlayerState

}