package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.ui.main.view_model.HostViewModel
import com.example.playlistmaker.ui.media.favorite.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.ui.media.playlists.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.new_playlist.view_model.EditPlaylistViewModel
import com.example.playlistmaker.ui.new_playlist.view_model.NewPlaylistViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.search.view_model.SearchTracksViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    factory {
        MediaPlayer()
    }

    factory {
        FirebaseAnalytics.getInstance(androidContext())
    }

    viewModel {
        SearchTracksViewModel(get(), get(), get())
    }

    viewModel {
        PlayerViewModel(get(),get(), get(), get())
    }

    viewModel {
        HostViewModel(get(), get())
    }

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }

    viewModel {
        EditPlaylistViewModel(get())
    }

    viewModel {
        PlaylistViewModel(get())
    }


}