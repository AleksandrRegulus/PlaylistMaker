package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.domain.api.SharedPrefsRepository

class GetThemeUseCase(
    private val repository: SharedPrefsRepository
) {

    fun execute(): Boolean {
        return repository.getThemeFromSharedPrefs()
    }
}