package com.example.playlistmaker.ui.media.favorite.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.FavoriteInteractor
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteInteractor: FavoriteInteractor,
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<FavoriteState>()
    val stateLiveData: LiveData<FavoriteState> = _stateLiveData

    fun getFavoriteTracks() {
        viewModelScope.launch {
            favoriteInteractor.favoriteTracks().collect { result ->
                if (result.isEmpty()) _stateLiveData.postValue(FavoriteState.Empty)
                else _stateLiveData.postValue(FavoriteState.FavoriteContent(result))
            }
        }

    }
}