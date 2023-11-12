package com.example.playlistmaker.di

import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.search.impl.SaveSearchHistoryUseCase
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.settings.impl.GetThemeUseCase
import com.example.playlistmaker.domain.settings.impl.SaveThemeUseCase
import com.google.gson.Gson
import org.koin.dsl.module

val interactorModule = module {

    factory { Gson() }

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single {
        GetSearchHistoryUseCase(get(), get())
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

}