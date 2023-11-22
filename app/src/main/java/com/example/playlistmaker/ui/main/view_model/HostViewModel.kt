package com.example.playlistmaker.ui.main.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.impl.GetThemeUseCase

class HostViewModel(
    private val getThemeUseCase: GetThemeUseCase
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<Boolean>()
    val stateLiveData: LiveData<Boolean> = _stateLiveData

    init {
        _stateLiveData.postValue(getThemeUseCase.execute())
    }

}