package com.example.playlistmaker.ui.player.view_model

sealed interface PlayerState {

    object Default : PlayerState
    object Prepared : PlayerState
    data class Paused(val currentPosition: String) : PlayerState
    data class Playing(val currentPosition: String) : PlayerState

}