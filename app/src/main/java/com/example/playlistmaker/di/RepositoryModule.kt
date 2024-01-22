package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.db.impl.FavoriteRepositoryImpl
import com.example.playlistmaker.data.SharedPrefsRepositoryImpl
import com.example.playlistmaker.data.converters.FavTrackDbConvertor
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.example.playlistmaker.domain.db.FavoriteRepository
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

    factory { FavTrackDbConvertor() }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }
}