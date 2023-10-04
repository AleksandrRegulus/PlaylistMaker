package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.api.SharedPrefsRepository

class SaveThemeUseCase (
    private val repository: SharedPrefsRepository
) {

    fun execute(theme: Boolean) {
        return repository.saveThemeToSharedPrefs(theme)
    }
}