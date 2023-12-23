package com.example.playlistmaker.ui.main.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.settings.impl.GetThemeUseCase
import com.example.playlistmaker.domain.settings.impl.SaveThemeUseCase

class HostViewModel(
    private val getThemeUseCase: GetThemeUseCase,
    private val saveThemeUseCase: SaveThemeUseCase
) : ViewModel() {

    private val _darkTheme = MutableLiveData<Boolean>()
    val darkTheme: LiveData<Boolean> = _darkTheme
    fun saveTheme(newTheme: Boolean) {
        saveThemeUseCase.execute(newTheme)
        _darkTheme.postValue(newTheme)
    }

    private val trackLiveData = MutableLiveData<Track>()
    fun getTrack(): LiveData<Track> = trackLiveData
    fun setTrack(track: Track) {
        trackLiveData.postValue(track)
    }


    private val windowFocus = MutableLiveData<Boolean>()
    fun getWindowFocus(): LiveData<Boolean> = windowFocus
    fun setWindowFocus(hasFocus: Boolean) {
        windowFocus.value = hasFocus
    }


    init {
        _darkTheme.postValue(getThemeUseCase.execute())
    }

}