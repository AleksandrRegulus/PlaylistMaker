package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.ui.main.view_model.HostViewModel
import com.example.playlistmaker.ui.media.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.ui.media.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.view_model.SearchTracksViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    factory {
        MediaPlayer()
    }

    viewModel {
        SearchTracksViewModel(get(), get(), get())
    }

    viewModel { (previewUrl: String) ->
        PlayerViewModel(previewUrl, get())
    }

    viewModel {
        HostViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoriteTracksViewModel()
    }

    viewModel {
        PlaylistViewModel()
    }
}