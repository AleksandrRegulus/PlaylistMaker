package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.impl.GetThemeUseCase
import com.example.playlistmaker.domain.settings.impl.SaveThemeUseCase

class SettingsViewModel(
    private val saveThemeUseCase: SaveThemeUseCase,
    private val getThemeUseCase: GetThemeUseCase,
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<SettingsState>()
    val stateLiveData: LiveData<SettingsState> = _stateLiveData

    init {
        _stateLiveData.value = SettingsState(getThemeUseCase.execute())
    }

    fun saveTheme(darkTheme: Boolean) {
        saveThemeUseCase.execute(darkTheme)
        _stateLiveData.value = SettingsState(darkTheme)
    }

}