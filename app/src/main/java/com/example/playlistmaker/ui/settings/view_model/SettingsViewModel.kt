package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.impl.SaveThemeUseCase

class SettingsViewModel(
    private val saveThemeUseCase: SaveThemeUseCase,
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<SettingsState>()
    val stateLiveData: LiveData<SettingsState> = _stateLiveData

    fun saveTheme(darkTheme: Boolean) {
        saveThemeUseCase.execute(darkTheme)
        _stateLiveData.postValue(SettingsState(darkTheme))
    }

}