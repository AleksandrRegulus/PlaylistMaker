package com.example.playlistmaker.ui.main.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.impl.GetThemeUseCase

class MainViewModel(
    private val getThemeUseCase: GetThemeUseCase
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<MainState>()
    val stateLiveData: LiveData<MainState> = _stateLiveData

    private fun renderState(state: MainState) {
        _stateLiveData.value = state
    }
    fun getTheme() {
        renderState(
            MainState(getThemeUseCase.execute())
        )
    }

}