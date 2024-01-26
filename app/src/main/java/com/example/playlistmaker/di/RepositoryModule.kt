package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.db.impl.FavoriteRepositoryImpl
import com.example.playlistmaker.data.SharedPrefsRepositoryImpl
import com.example.playlistmaker.data.converters.FavTrackDbConverter
import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.PlaylistsTrackDbConverter
import com.example.playlistmaker.data.db.impl.PlaylistsRepositoryImpl
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.example.playlistmaker.domain.db.FavoriteRepository
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single {
        androidContext()
            .getSharedPreferences("playlist_maker_preferences", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single< SharedPrefsRepository> {
        SharedPrefsRepositoryImpl(get(), get(), get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    factory { FavTrackDbConverter() }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }

    factory { PlaylistDbConverter() }

    factory { PlaylistsTrackDbConverter() }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get(), get(), get())
    }
}