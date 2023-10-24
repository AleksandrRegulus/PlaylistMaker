package com.example.playlistmaker.di

import com.example.playlistmaker.ui.main.view_model.MainViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.view_model.SearchTracksViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchTracksViewModel(get(), get(), get())
    }

    viewModel { (previewUrl: String) ->
        PlayerViewModel(previewUrl)
    }

    viewModel {
        MainViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(),get())
    }
}