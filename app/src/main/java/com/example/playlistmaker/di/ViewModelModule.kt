package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.ui.main.view_model.HostViewModel
import com.example.playlistmaker.ui.media.favorite.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.ui.media.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.view_model.SearchTracksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    factory {
        MediaPlayer()
    }

    viewModel {
        SearchTracksViewModel(get(), get(), get())
    }

    viewModel {
        PlayerViewModel(get(),get())
    }

    viewModel {
        HostViewModel(get(), get())
    }

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        PlaylistViewModel()
    }
}