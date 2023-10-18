package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.domain.api.SharedPrefsRepository

class SaveThemeUseCase (
    private val repository: SharedPrefsRepository
) {

    fun execute(theme: Boolean) {
        return repository.saveThemeToSharedPrefs(theme)
    }
}