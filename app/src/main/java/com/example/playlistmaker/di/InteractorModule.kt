package com.example.playlistmaker.di

import com.example.playlistmaker.domain.db.FavoriteInteractor
import com.example.playlistmaker.domain.db.PlaylistsInteractor
import com.example.playlistmaker.domain.db.impl.FavoriteInteractorImpl
import com.example.playlistmaker.domain.db.impl.PlaylistsInteractorImpl
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.search.impl.SaveSearchHistoryUseCase
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.settings.impl.GetThemeUseCase
import com.example.playlistmaker.domain.settings.impl.SaveThemeUseCase
import org.koin.dsl.module

val interactorModule = module {

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single {
        GetSearchHistoryUseCase(get())
    }

    single {
        SaveSearchHistoryUseCase(get(), get())
    }

    single {
        GetThemeUseCase(get())
    }

    single {
        SaveThemeUseCase(get())
    }

    single<FavoriteInteractor> {
        FavoriteInteractorImpl(get())
    }

    single<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }
}