package com.example.playlistmaker.ui.main.view_model

sealed interface MainState {
    object DarkTheme: MainState
    object LightTheme: MainState
}