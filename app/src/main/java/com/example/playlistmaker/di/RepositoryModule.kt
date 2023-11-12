package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.SharedPrefsRepositoryImpl
import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single {
        androidContext()
            .getSharedPreferences("playlist_maker_preferences", Context.MODE_PRIVATE)
    }

    single< SharedPrefsRepository> {
        SharedPrefsRepositoryImpl(get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }
}