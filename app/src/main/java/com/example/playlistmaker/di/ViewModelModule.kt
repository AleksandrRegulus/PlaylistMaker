package com.example.playlistmaker.di

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.ui.main.view_model.MainViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.view_model.SearchTracksViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    factory {
        MediaPlayer()
    }

    single {
        Handler(Looper.getMainLooper())
    }

    viewModel {
        SearchTracksViewModel(get(), get(), get(), get())
    }

    viewModel { (previewUrl: String) ->
        PlayerViewModel(previewUrl, get(), get())
    }

    viewModel {
        MainViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }
}