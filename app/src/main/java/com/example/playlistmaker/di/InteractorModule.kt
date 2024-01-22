package com.example.playlistmaker.di

import com.example.playlistmaker.domain.db.FavoriteInteractor
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.db.impl.FavoriteInteractorImpl
import com.example.playlistmaker.domain.db.impl.PlaylistInteractorImpl
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.search.impl.SaveSearchHistoryUseCase
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.settings.impl.GetThemeUseCase
import com.example.playlistmaker.domain.settings.impl.SaveThemeUseCase
import org.koin.dsl.module
import kotlin.math.sin

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

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }
}